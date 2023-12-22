package pl.mpietrewicz.sp.modules.balance.domain.balance;

import lombok.NoArgsConstructor;
import pl.mpietrewicz.sp.ddd.annotations.domain.AggregateRoot;
import pl.mpietrewicz.sp.ddd.canonicalmodel.publishedlanguage.AggregateId;
import pl.mpietrewicz.sp.ddd.canonicalmodel.publishedlanguage.MonthlyBalance;
import pl.mpietrewicz.sp.ddd.canonicalmodel.publishedlanguage.PaymentPolicy;
import pl.mpietrewicz.sp.ddd.canonicalmodel.publishedlanguage.snapshot.ContractData;
import pl.mpietrewicz.sp.ddd.support.domain.DomainEventPublisher;
import pl.mpietrewicz.sp.modules.balance.ddd.support.domain.BaseAggregateRoot;
import pl.mpietrewicz.sp.modules.balance.domain.balance.month.ComponentPremium;
import pl.mpietrewicz.sp.modules.balance.domain.balance.operation.Operation;
import pl.mpietrewicz.sp.modules.balance.domain.balance.operation.type.AddPayment;
import pl.mpietrewicz.sp.modules.balance.domain.balance.operation.type.AddRefund;
import pl.mpietrewicz.sp.modules.balance.domain.balance.operation.type.ChangePremium;
import pl.mpietrewicz.sp.modules.balance.domain.balance.operation.type.StartCalculating;

import javax.inject.Inject;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.Transient;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static java.util.function.Predicate.not;

@AggregateRoot
@Entity
@NoArgsConstructor
public class Balance extends BaseAggregateRoot {

    private ContractData contractData;

    private final int grace = 3;

    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "balance_id")
    private final List<Operation> operations = new ArrayList<>();

    @Transient
    @Inject
    protected DomainEventPublisher eventPublisher;

    public Balance(AggregateId aggregateId, ContractData contractData) {
        this.aggregateId = aggregateId;
        this.contractData = contractData;
    }

    public void startCalculating(LocalDate date, BigDecimal premium, AggregateId componentId) {
        StartCalculating startCalculating = new StartCalculating(YearMonth.from(date), premium, componentId);
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

    private void commit(StartCalculating operation) {
        operation.execute(grace);

        operations.add(operation);
        publishUpdatedBalance();
    }

    private void commit(Operation operation) {
        calculate(operation);
        recalculateAfter(operation);

        operations.add(operation);
        publishUpdatedBalance();
    }

    private void calculate(Operation operation) {
        Operation previousOperation = getPreviousOperation(operation);
        operation.execute(previousOperation, grace);
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
                .map(StartCalculating.class::cast)
                .findAny()
                .orElseThrow();  // todo: dodać wyjątek że musi istnieć chociaż startCalculating
    }

}