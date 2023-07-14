package pl.mpietrewicz.sp.modules.balance.domain.balance;

import lombok.NoArgsConstructor;
import pl.mpietrewicz.sp.ddd.annotations.domain.AggregateRoot;
import pl.mpietrewicz.sp.ddd.canonicalmodel.publishedlanguage.AggregateId;
import pl.mpietrewicz.sp.ddd.canonicalmodel.publishedlanguage.Frequency;
import pl.mpietrewicz.sp.ddd.canonicalmodel.publishedlanguage.snapshot.ContractData;
import pl.mpietrewicz.sp.ddd.support.domain.BaseAggregateRoot;
import pl.mpietrewicz.sp.ddd.sharedkernel.PaymentPolicyEnum;
import pl.mpietrewicz.sp.modules.balance.domain.balance.operation.AddPayment;
import pl.mpietrewicz.sp.modules.balance.domain.balance.operation.AddPeriod;
import pl.mpietrewicz.sp.modules.balance.domain.balance.operation.AddPremium;
import pl.mpietrewicz.sp.modules.balance.domain.balance.operation.AddRefund;
import pl.mpietrewicz.sp.modules.balance.domain.balance.operation.ChangeFrequency;
import pl.mpietrewicz.sp.modules.balance.domain.balance.operation.DeletePremium;
import pl.mpietrewicz.sp.modules.balance.domain.balance.operation.StartCalculating;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static java.util.function.Predicate.not;

@AggregateRoot
@Entity
@NoArgsConstructor
public class Balance extends BaseAggregateRoot {

    private ContractData contractData;

    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "balance_id")
    private List<Operation> operations;

    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "balance_id")
    private List<Operation> pendingOperations;


    public Balance(AggregateId aggregateId, ContractData contractData, List<Operation> operations, List<Operation> pendingOperations) {
        this.aggregateId = aggregateId;
        this.contractData = contractData;
        this.operations = operations;
        this.pendingOperations = pendingOperations;
    }

    public void addPremium(LocalDate start, BigDecimal premium) {
        AddPremium addPremium = new AddPremium(start, premium);
        commit(addPremium);
    }

    public void deletePremium(LocalDate start, BigDecimal premium) {
        DeletePremium deletePremium = new DeletePremium(start, premium);
        commit(deletePremium);
    }

    public void addPayment(LocalDate date, BigDecimal amount, PaymentPolicyEnum paymentPolicyEnum) {
        AddPayment addPayment = new AddPayment(date, amount, paymentPolicyEnum);
        commit(addPayment);
    }

    public void addRefund(LocalDate date, BigDecimal amount) {
        AddRefund addRefund = new AddRefund(date, amount);
        commit(addRefund);
    }

    public void changeFrequency(LocalDate date, Frequency frequency) {
        ChangeFrequency changeFrequency = new ChangeFrequency(date, frequency);
        commit(changeFrequency);
    }

    public void openMonth(YearMonth month) {
        Optional<StartCalculating> pendingStartCalculating = getPendingStartCalculating();
        if (pendingStartCalculating.isPresent()) {
            if (pendingStartCalculating.get().getFrom().equals(month)) {
                pendingStartCalculating.get().calculate();
                operations.add(pendingStartCalculating.get());
                pendingOperations.remove(pendingStartCalculating.get());
                // todo: wyznacz przypis
            }
        } else {
            YearMonth lastAfectedMonth = getLastAfectedMonth();
            Frequency frequency = getLastFrequency();
            if (lastAfectedMonth.plusMonths(1).equals(month)) {
                AddPeriod addPeriod = new AddPeriod(month, frequency);
                commit(addPeriod);
                // todo: wyznacz przypis
            }
            List<Operation> operationsToExecute = getPendingOperationsToExecute(month, frequency);
            for (Operation operation : operationsToExecute) {
                commit(operation);
            }
            // todo: koryguj przypis - o ile były inne operacje
            // todo: albo nie rozdzielać na nowe i korygowane i zawsze porównywać to co było z tym co jest po zmianach
        }
    }

    private void commit(Operation operation) { // todo: informacyjnie: zacommitować można tylko raz operacje
        if (operation instanceof AddPeriod || !isFromTheFuture(operation)) {
            Operation previousOperation = getPreviousOperation(operation);
            operation.execute(previousOperation.getPeriodCopy());
            operations.add(operation);
            pendingOperations.remove(operation);

            reexecuteAfter(operation);
            // todo: wyznaczyć przypis -> wysłać period z ostatniej operacji
            List<Allocationlish> allocationlishes = operations.stream()
                    .max(Operation::orderComparator)
                    .map(Operation::getAllocationlishList)
                    .orElseThrow();
            // todo: wywołać zdarzenie wyznaczenia przypisu

        } else {
            pendingOperations.add(operation);
        }
    }

    private Frequency getLastFrequency() {
        return operations.stream()
                .filter(operation -> operation.getFrequency().isPresent())
                .max(Operation::orderComparator)
                .map(Operation::getFrequency)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .orElseThrow();
    }

    private List<Operation> getPendingOperationsToExecute(YearMonth month, Frequency frequency) {
        return pendingOperations.stream()
                .filter(operation -> YearMonth.from(operation.getDate()).compareTo(month) <= 0) // todo: do poprawnej implementajci z czestotliwością
                .sorted(Operation::orderComparator)
                .collect(Collectors.toList());
    }

    private void reexecuteAfter(Operation operation) {
        Operation previousOperation = operation;
        List<Operation> nextOperations = getNextOperationsAfter(operation);
        for (Operation nextOperation : nextOperations) {
            nextOperation.execute(previousOperation.getPeriodCopy());
            previousOperation = nextOperation;
        }
    }

    private Operation getPreviousOperation(Operation operation) {
        return operations.stream()
                .filter(o -> o.isBefore(operation))
                .max(Operation::orderComparator)
                .orElse(getStartCalculatingOperation());
    }

    private List<Operation> getNextOperationsAfter(Operation operation) {
        return operations.stream()
                .filter(not(StartCalculating.class::isInstance))
                .filter(o -> o.isAfter(operation))
                .sorted(Operation::orderComparator)
                .collect(Collectors.toList());
    }

    private boolean isFromTheFuture(Operation operation) {
        return operations.stream()
                .max(Operation::orderComparator)
                .map(Operation::getLastAfectedMonth)
                .map(lastAfectedMonth -> YearMonth.from(operation.getDate()).isAfter(lastAfectedMonth))
                .orElse(true);
    }

    private YearMonth getLastAfectedMonth() {
        return operations.stream()
                .filter(operation -> operation instanceof StartCalculating
                        || operation instanceof AddPeriod)
                .max(Operation::orderComparator)
                .map(Operation::getLastAfectedMonth)
                .orElseThrow(() -> new IllegalStateException("Balance is not started!"));
    }

    private Optional<StartCalculating> getPendingStartCalculating() {
        return pendingOperations.stream()
                .filter(StartCalculating.class::isInstance)
                .map(StartCalculating.class::cast)
                .findAny();
    }

    private Operation getStartCalculatingOperation() {
        return operations.stream()
                .filter(StartCalculating.class::isInstance)
                .findAny()
                .orElseThrow();
    }

}