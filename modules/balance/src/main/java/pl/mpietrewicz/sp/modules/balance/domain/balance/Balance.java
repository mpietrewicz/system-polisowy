package pl.mpietrewicz.sp.modules.balance.domain.balance;

import lombok.Getter;
import pl.mpietrewicz.sp.ddd.annotations.domain.AggregateRoot;
import pl.mpietrewicz.sp.ddd.canonicalmodel.publishedlanguage.AggregateId;
import pl.mpietrewicz.sp.ddd.canonicalmodel.publishedlanguage.PaymentPolicyEnum;
import pl.mpietrewicz.sp.ddd.canonicalmodel.publishedlanguage.snapshot.PaymentData;
import pl.mpietrewicz.sp.ddd.canonicalmodel.publishedlanguage.snapshot.RefundData;
import pl.mpietrewicz.sp.ddd.sharedkernel.Amount;
import pl.mpietrewicz.sp.ddd.support.domain.DomainEventPublisher;
import pl.mpietrewicz.sp.modules.balance.domain.balance.operation.Operation;
import pl.mpietrewicz.sp.modules.balance.domain.balance.operation.type.AddPayment;
import pl.mpietrewicz.sp.modules.balance.domain.balance.operation.type.AddRefund;
import pl.mpietrewicz.sp.modules.balance.domain.balance.operation.type.CancelStopCalculating;
import pl.mpietrewicz.sp.modules.balance.domain.balance.operation.type.ChangePremium;
import pl.mpietrewicz.sp.modules.balance.domain.balance.operation.type.StartCalculating;
import pl.mpietrewicz.sp.modules.balance.domain.balance.operation.type.StopCalculating;
import pl.mpietrewicz.sp.modules.balance.domain.balance.publisherpolicy.PublishPolicy;
import pl.mpietrewicz.sp.modules.balance.exceptions.BalanceStoppedException;
import pl.mpietrewicz.sp.modules.balance.exceptions.NoOperationException;
import pl.mpietrewicz.sp.modules.balance.exceptions.ReexecutionException;
import pl.mpietrewicz.sp.modules.balance.exceptions.UnavailabilityException;
import pl.mpietrewicz.sp.modules.contract.application.api.PremiumService;

import javax.inject.Inject;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@AggregateRoot
@Getter
public class Balance {

    @Inject
    protected DomainEventPublisher eventPublisher;

    @Inject
    protected PremiumService premiumService;

    @Inject
    protected List<PublishPolicy> publishPolicies;

    private final AggregateId aggregateId;

    private final Long version;

    private final AggregateId contractId;

    private final List<Operation> operations;

    public Balance(AggregateId aggregateId, Long version, AggregateId contractId, List<Operation> operations) {
        this.aggregateId = aggregateId;
        this.version = version;
        this.contractId = contractId;
        this.operations = operations;
    }

    public Balance(AggregateId aggregateId, Long version, AggregateId contractId, StartCalculating startCalculating) {
        this.aggregateId = aggregateId;
        this.version = version;
        this.contractId = contractId;
        this.operations = Stream.of(startCalculating).collect(Collectors.toList());
    }

    public void addPayment(PaymentData paymentData, PaymentPolicyEnum paymentPolicyEnum) {
        AggregateId paymentId = paymentData.getAggregateId();
        LocalDate date = paymentData.getDate();
        Amount amount = paymentData.getAmount();

        AddPayment addPayment = new AddPayment(paymentId, date, amount, paymentPolicyEnum, eventPublisher, premiumService);
        commit(addPayment);
    }

    public void addRefund(RefundData refundData) {
        AggregateId refundId = refundData.getAggregateId();
        LocalDate date = refundData.getDate();
        Amount amount = refundData.getAmount();

        AddRefund addRefund = new AddRefund(refundId, date, amount, eventPublisher);
        commit(addRefund);
    }

    public void changePremium(LocalDate date, LocalDateTime timestamp) {
        ChangePremium changePremium = new ChangePremium(date, timestamp, eventPublisher, premiumService);
        commit(changePremium);
    }

    public void stopCalculating(LocalDate end) {
        try {
            tryStopCalculating(end);
        } catch (BalanceStoppedException e) {
            StopCalculating.handle(new UnavailabilityException(contractId, e, "Unable to stop calculating " +
                    "balance balance is currently stopped", contractId.getId()), eventPublisher);
        }
    }

    private void tryStopCalculating(LocalDate end) throws BalanceStoppedException {
        if (getStopCalculating().isPresent()) throw new BalanceStoppedException();
        StopCalculating stopCalculating = new StopCalculating(end, eventPublisher);
        commit(stopCalculating);
    }

    public void cancelStopCalculating() {
        try {
            tryCancelStopCalculating();
        } catch (NoOperationException e) {
            CancelStopCalculating.handle(new UnavailabilityException(contractId, e, "Unable to cancel stop calculating " +
                    "balance balance isn't stop on contract {}", contractId.getId()), eventPublisher);
        }
    }

    private void tryCancelStopCalculating() throws NoOperationException {
        StopCalculating stopCalculating = tryGetStopCalculating();
        CancelStopCalculating cancelStopCalculating = new CancelStopCalculating(stopCalculating, eventPublisher);
        commit(cancelStopCalculating);
    }

    private void commit(Operation operation) {
        PeriodProvider before = getLastOperation().getPeriod();

        calculate(operation);
        operations.add(operation);
        recalculateAfter(operation);

        PeriodProvider after = getLastOperation().getPeriod();
        publishUpdatedBalanceResult(before, after);
    }

    private void calculate(Operation operation) {
        Operation previousOperation = getPreviousOperation(operation);
        operation.execute(previousOperation, contractId);
    }

    private void recalculateAfter(Operation operation) {
        try {
            tryRecalculateAfter(operation);
        } catch (ReexecutionException e) {
            operation.handle(contractId, e);
        }
    }

    private void tryRecalculateAfter(Operation operation) throws ReexecutionException {
        List<Operation> nextOperations = getNextOperationsAfter(operation);
        for (Operation nextOperation : nextOperations) {
            recalculate(nextOperation, operation.getRegistration(), prepareInfo(operation));
        }
    }

    private void recalculate(Operation operation, LocalDateTime registration, String info) throws ReexecutionException {
        Operation previousOperation = getPreviousOperation(operation);
        operation.reexecute(previousOperation, contractId, registration, info);
    }

    private void publishUpdatedBalanceResult(PeriodProvider before, PeriodProvider after) {
        for (PublishPolicy publishPolicy : publishPolicies) {
            publishPolicy.doPublish(contractId, before, after);
        }
    }

    private StopCalculating tryGetStopCalculating() throws NoOperationException {
        return getStopCalculating()
                .orElseThrow(() -> new NoOperationException(new NoSuchElementException(),
                        "No stop calculating operation on contract {}", contractId.getId()));
    }

    private List<Operation> getValidOperations() {
        return operations.stream()
                .filter(Operation::isValid)
                .collect(Collectors.toList());
    }

    private Operation getPreviousOperation(Operation operation) {
        return getValidOperations().stream()
                .filter(o -> o.isBefore(operation))
                .max(Operation::orderComparator)
                .orElse(getStartCalculating());
    }

    private List<Operation> getNextOperationsAfter(Operation operation) {
        return getValidOperations().stream()
                .filter(o -> o.isAfter(operation))
                .sorted(Operation::orderComparator)
                .collect(Collectors.toList());
    }

    private Operation getLastOperation() {
        return getValidOperations().stream()
                .max(Operation::orderComparator)
                .orElseThrow();
    }

    private Operation getStartCalculating() {
        return getValidOperations().stream()
                .filter(StartCalculating.class::isInstance)
                .findAny()
                .orElseThrow();
    }

    private Optional<StopCalculating> getStopCalculating() {
        return getValidOperations().stream()
                .filter(StopCalculating.class::isInstance)
                .filter(Operation::isValid)
                .map(StopCalculating.class::cast)
                .findAny();
    }

    private String prepareInfo(Operation operation) { // todo: do poprawy logowanie
        return "reexecute after " + operation.getClass().getName() + " : " + operation.getDate();
    }

}