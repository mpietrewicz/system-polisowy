package pl.mpietrewicz.sp.modules.balance.domain.balance;

import lombok.NoArgsConstructor;
import pl.mpietrewicz.sp.ddd.annotations.domain.AggregateRoot;
import pl.mpietrewicz.sp.ddd.canonicalmodel.publishedlanguage.AggregateId;
import pl.mpietrewicz.sp.ddd.canonicalmodel.publishedlanguage.Frequency;
import pl.mpietrewicz.sp.ddd.canonicalmodel.publishedlanguage.MonthlyBalance;
import pl.mpietrewicz.sp.ddd.canonicalmodel.publishedlanguage.PaymentPolicy;
import pl.mpietrewicz.sp.ddd.canonicalmodel.publishedlanguage.snapshot.ContractData;
import pl.mpietrewicz.sp.ddd.support.domain.DomainEventPublisher;
import pl.mpietrewicz.sp.modules.balance.ddd.support.domain.BaseAggregateRoot;
import pl.mpietrewicz.sp.modules.balance.domain.balance.month.ComponentPremium;
import pl.mpietrewicz.sp.modules.balance.domain.balance.operation.AddPayment;
import pl.mpietrewicz.sp.modules.balance.domain.balance.operation.AddRefund;
import pl.mpietrewicz.sp.modules.balance.domain.balance.operation.StopCalculating;
import pl.mpietrewicz.sp.modules.balance.domain.balance.operation.ChangePremium;
import pl.mpietrewicz.sp.modules.balance.domain.balance.operation.StartCalculating;

import javax.inject.Inject;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.Transient;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static java.util.function.Predicate.not;

@AggregateRoot
@Entity
@NoArgsConstructor
public class Balance extends BaseAggregateRoot {

    private ContractData contractData;

    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "balance_id")
    private Set<Operation> operations = new HashSet<>();

    @Transient
    @Inject
    protected DomainEventPublisher eventPublisher;

    public Balance(AggregateId aggregateId, ContractData contractData) {
        this.aggregateId = aggregateId;
        this.contractData = contractData;
    }

    public void startCalculating(LocalDate date, BigDecimal premium, Frequency frequency, AggregateId componentId) {
        StartCalculating startCalculating = new StartCalculating(YearMonth.from(date), premium, frequency, componentId);
        commit(startCalculating);
    }

    public void addPayment(LocalDate date, BigDecimal amount, PaymentPolicy paymentPolicy) {
        AddPayment addPayment = new AddPayment(date, amount, paymentPolicy);
        commit(addPayment);
    }

    public void addRefund(LocalDate date, BigDecimal amount) {
        AddRefund addRefund = new AddRefund(date, amount);
        commit(addRefund);
    }

    public void changePremium(LocalDate start, BigDecimal premium, AggregateId componentId) {
        ComponentPremium componentPremium = new ComponentPremium(componentId, premium);
        ChangePremium changePremium = new ChangePremium(start, componentPremium);
        commit(changePremium);
    }

    public void stopCalculating(LocalDate date, Frequency frequency) {
        StopCalculating stopCalculating = new StopCalculating(date, frequency);
        commit(stopCalculating);
    }

    private void commit(StartCalculating operation) {
        operations.add(operation);
        calculate(operation);
        recalculateAfter(operation);
        publishUpdatedBalance();
    }

    private void calculate(StartCalculating operation) {
        operation.calculate();
    }

    private void commit(Operation operation) {
        operations.add(operation);
        calculate(operation);
        recalculateAfter(operation);
        publishUpdatedBalance();
    }

    private void commit(StopCalculating operation) {
        operations.add(operation);
        calculate(operation);
        publishUpdatedBalance();
    }

    private void calculate(Operation operation) {
        Operation previousOperation = getPreviousOperation(operation);
        operation.calculate(previousOperation);
    }

    private void recalculateAfter(Operation operation) {
        for (Operation nextOperation : getNextOperationsAfter(operation)) {
            calculate(nextOperation);
        }
    }

    private List<Operation> getExecutedOperations() {
        return operations.stream()
                .filter(not(Operation::isPending))
                .collect(Collectors.toList());
    }

    private void publishUpdatedBalance() {
        List<MonthlyBalance> monthlyBalances = getLastOperation().getMonthlyBalances();
//        eventPublisher.publish(new BalanceUpdatedEvent(contractData, monthlyBalances));
    }

    private Operation getPreviousOperation(Operation operation) {
        return getExecutedOperations().stream()
                .filter(o -> o.isBefore(operation))
                .max(Operation::orderComparator)
                .orElse(getStartCalculating());
    }

    private List<Operation> getNextOperationsAfter(Operation operation) {
        return getExecutedOperations().stream()
                .filter(not(Operation::isStartCalculatingOperation))
                .filter(o -> o.isAfter(operation))
                .sorted(Operation::orderComparator)
                .collect(Collectors.toList());
    }

    private Operation getLastOperation() {
        return getExecutedOperations().stream()
                .max(Operation::orderComparator)
                .orElseThrow(); // todo: dodać wyjątek że musi istnieć chociaż startCalculating
    }

    private StartCalculating getStartCalculating() {
        return operations.stream()
                .filter(Operation::isStartCalculatingOperation)
                .map(StartCalculating.class::cast)
                .findAny()
                .orElseThrow();  // todo: dodać wyjątek że musi istnieć chociaż startCalculating
    }

}