package pl.mpietrewicz.sp.modules.balance.domain.balance;

import lombok.Getter;
import pl.mpietrewicz.sp.ddd.annotations.domain.AggregateRoot;
import pl.mpietrewicz.sp.ddd.canonicalmodel.publishedlanguage.AggregateId;
import pl.mpietrewicz.sp.ddd.canonicalmodel.publishedlanguage.MonthlyBalance;
import pl.mpietrewicz.sp.ddd.canonicalmodel.publishedlanguage.PaymentPolicyEnum;
import pl.mpietrewicz.sp.ddd.canonicalmodel.publishedlanguage.snapshot.premium.PremiumSnapshot;
import pl.mpietrewicz.sp.ddd.sharedkernel.Amount;
import pl.mpietrewicz.sp.ddd.sharedkernel.PositiveAmount;
import pl.mpietrewicz.sp.ddd.support.domain.DomainEventPublisher;
import pl.mpietrewicz.sp.modules.balance.domain.balance.operation.Operation;
import pl.mpietrewicz.sp.modules.balance.domain.balance.operation.type.AddPayment;
import pl.mpietrewicz.sp.modules.balance.domain.balance.operation.type.AddRefund;
import pl.mpietrewicz.sp.modules.balance.domain.balance.operation.type.ChangePremium;
import pl.mpietrewicz.sp.modules.balance.domain.balance.operation.type.StartCalculating;
import pl.mpietrewicz.sp.modules.balance.exceptions.ReexecutionException;
import pl.mpietrewicz.sp.modules.contract.application.api.PremiumService;

import javax.inject.Inject;
import javax.persistence.RollbackException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

@AggregateRoot
@Getter
public class Balance {

    private final AggregateId aggregateId;

    private final Long version;

    private final AggregateId contractId;

    private final List<Operation> operations;

    @Inject
    protected DomainEventPublisher eventPublisher;

    @Inject
    protected PremiumService premiumService;

    public Balance(AggregateId aggregateId, Long version, AggregateId contractId, List<Operation> operations) {
        this.aggregateId = aggregateId;
        this.version = version;
        this.contractId = contractId;
        this.operations = operations;
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

    public void changePremium(LocalDate date, PremiumSnapshot premiumSnapshot) {
        PositiveAmount premium = premiumSnapshot.getAmountAt(date);
        AggregateId premiumId = premiumSnapshot.getPremiumId();
        LocalDateTime timestamp = premiumSnapshot.getTimestamp();

        ChangePremium changePremium = new ChangePremium(date, premium.getAmount(), premiumId, timestamp);
        commit(changePremium);
    }

    private void commit(StartCalculating operation) {
        operation.execute();
        operations.add(operation);

        publishUpdatedBalanceAfter(operation);
    }

    private void commit(Operation operation) {
        PremiumSnapshot premiumSnapshot = premiumService.getPremiumSnapshot(contractId, operation.getRegistration());
        calculate(operation, premiumSnapshot);

        operations.add(operation);
        Operation lastRecalculated = recalculateAfter(operation, premiumSnapshot);

        publishUpdatedBalanceAfter(lastRecalculated);
    }

    private void calculate(Operation operation, PremiumSnapshot premiumSnapshot) {
        Operation previousOperation = getPreviousOperation(operation);
        operation.execute(previousOperation, premiumSnapshot, eventPublisher);
    }

    private Operation recalculateAfter(Operation operation, PremiumSnapshot premiumSnapshot) {
        try {
            return tryRecalculateAfter(operation, premiumSnapshot);
        } catch (ReexecutionException e) {
            operation.handle(e, eventPublisher);
            throw new RollbackException(e);
        }
    }

    private Operation tryRecalculateAfter(Operation operation, PremiumSnapshot premiumSnapshot) throws ReexecutionException {
        List<Operation> nextOperations = getNextOperationsAfter(operation);

        Iterator<Operation> operationIterator = nextOperations.iterator();
        while (operationIterator.hasNext()) {
            Operation nextOperation = operationIterator.next();
            recalculate(nextOperation, premiumSnapshot);
            if (!operationIterator.hasNext()) return nextOperation;
        }

        return operation;
    }

    private void recalculate(Operation operation, PremiumSnapshot premiumSnapshot) throws ReexecutionException {
        Operation previousOperation = getPreviousOperation(operation);
        operation.reexecute(previousOperation, premiumSnapshot, eventPublisher);
    }

    private void publishUpdatedBalanceAfter(Operation operation) {
        PremiumSnapshot premiumSnapshot = premiumService.getPremiumSnapshot(contractId, operation.getRegistration());

        List<MonthlyBalance> monthlyBalances = getLastOperation().getMonthlyBalances(premiumSnapshot);
//        eventPublisher.publish(new BalanceUpdatedEvent(contractData, monthlyBalances), "BalanceServiceImpl");
    }

    private Operation getPreviousOperation(Operation operation) {
        return operations.stream()
                .filter(o -> o.isBefore(operation))
                .max(Operation::orderComparator)
                .orElse(getStartCalculating());
    }

    private List<Operation> getNextOperationsAfter(Operation operation) {
        return operations.stream()
                .filter(o -> o.isAfter(operation))
                .sorted(Operation::orderComparator)
                .collect(Collectors.toList());
    }

    private Operation getLastOperation() {
        return operations.stream()
                .max(Operation::orderComparator)
                .orElseThrow();
    }

    private StartCalculating getStartCalculating() {
        return operations.stream()
                .map(StartCalculating.class::cast)
                .findAny()
                .orElseThrow();
    }

}