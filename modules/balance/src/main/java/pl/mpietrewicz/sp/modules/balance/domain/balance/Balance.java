package pl.mpietrewicz.sp.modules.balance.domain.balance;

import lombok.NoArgsConstructor;
import pl.mpietrewicz.sp.ddd.annotations.domain.AggregateRoot;
import pl.mpietrewicz.sp.ddd.canonicalmodel.publishedlanguage.AggregateId;
import pl.mpietrewicz.sp.ddd.canonicalmodel.publishedlanguage.PaymentPolicyEnum;
import pl.mpietrewicz.sp.ddd.canonicalmodel.publishedlanguage.snapshot.ContractData;
import pl.mpietrewicz.sp.ddd.canonicalmodel.publishedlanguage.snapshot.premium.PremiumSnapshot;
import pl.mpietrewicz.sp.ddd.sharedkernel.Amount;
import pl.mpietrewicz.sp.ddd.sharedkernel.PositiveAmount;
import pl.mpietrewicz.sp.ddd.support.domain.DomainEventPublisher;
import pl.mpietrewicz.sp.modules.balance.ddd.support.domain.BaseAggregateRoot;
import pl.mpietrewicz.sp.modules.balance.domain.balance.operation.Operation;
import pl.mpietrewicz.sp.modules.balance.domain.balance.operation.type.AddPayment;
import pl.mpietrewicz.sp.modules.balance.domain.balance.operation.type.AddRefund;
import pl.mpietrewicz.sp.modules.balance.domain.balance.operation.type.ChangePremium;
import pl.mpietrewicz.sp.modules.balance.domain.balance.operation.type.StartCalculating;
import pl.mpietrewicz.sp.modules.contract.application.api.PremiumService;

import javax.inject.Inject;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.Transient;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.Iterator;
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

    @Transient
    @Inject
    protected PremiumService premiumService;

    public Balance(AggregateId aggregateId, ContractData contractData) {
        this.aggregateId = aggregateId;
        this.contractData = contractData;
    }

    public void startCalculating(LocalDate date, PremiumSnapshot premiumSnapshot) {
        PositiveAmount premium = premiumSnapshot.getAmountAt(date);
        StartCalculating startCalculating = new StartCalculating(YearMonth.from(date), premium.getAmount());
        commit(startCalculating);
    }

    public void addPayment(LocalDate date, Amount payment, PaymentPolicyEnum paymentPolicyEnum) {
        AddPayment addPayment = new AddPayment(date, payment, paymentPolicyEnum);
        commit(addPayment);
    }

    public void addRefund(LocalDate date, Amount refund) {
        AddRefund addRefund = new AddRefund(date, refund);
        commit(addRefund);
    }

    public void changePremium(LocalDate start, PremiumSnapshot premiumSnapshot) {
        PositiveAmount premium = premiumSnapshot.getAmountAt(start);
        ChangePremium changePremium = new ChangePremium(start, premium.getAmount());
        commit(changePremium);
    }

    private void commit(StartCalculating operation) {
        operation.execute();
        operations.add(operation);

        publishUpdatedBalanceAfter(operation);
    }

    private void commit(Operation operation) {
        AggregateId contractId = contractData.getAggregateId();
        LocalDateTime registration = operation.getRegistration();
        PremiumSnapshot premiumSnapshot = premiumService.getPremiumSnapshot(contractId, registration);

        calculate(operation, premiumSnapshot);
        operations.add(operation);
        Operation lastRecalculated = recalculateAfter(operation, premiumSnapshot);

        publishUpdatedBalanceAfter(lastRecalculated);
    }

    private void calculate(Operation operation, PremiumSnapshot premiumSnapshot) {
        Operation previousOperation = getPreviousOperation(operation);
        operation.execute(previousOperation, premiumSnapshot);
    }

    private Operation recalculateAfter(Operation operation, PremiumSnapshot premiumSnapshot) {
        List<Operation> nextOperations = getNextOperationsAfter(operation);

        Iterator<Operation> operationIterator = nextOperations.iterator();
        while (operationIterator.hasNext()) {
            Operation nextOperation = operationIterator.next();
            calculate(nextOperation, premiumSnapshot);
            if (!operationIterator.hasNext()) return nextOperation;
        }

        return operation;
    }

    private List<Operation> getExecutedOperations() {
        return operations.stream()
                .filter(not(Operation::isPending))
                .collect(Collectors.toList());
    }

    private void publishUpdatedBalanceAfter(Operation operation) {
        AggregateId contractId = contractData.getAggregateId();
//        PremiumSnapshot premiumSnapshot = premiumService.getPremiumSnapshot(contractId, operation.getRegistration());
        // todo: mogę sobie rozksiegowac na aktualnie przekazanej liście składek (Premium)

//        List<MonthlyBalance> monthlyBalances = getLastOperation().getMonthlyBalances(premiumSnapshot);
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