package pl.mpietrewicz.sp.modules.balance.domain.balance;

import lombok.Getter;
import pl.mpietrewicz.sp.ddd.annotations.domain.AggregateRoot;
import pl.mpietrewicz.sp.ddd.canonicalmodel.publishedlanguage.AggregateId;
import pl.mpietrewicz.sp.ddd.canonicalmodel.publishedlanguage.PaymentPolicyEnum;
import pl.mpietrewicz.sp.ddd.canonicalmodel.publishedlanguage.snapshot.premium.PremiumSnapshot;
import pl.mpietrewicz.sp.ddd.sharedkernel.Amount;
import pl.mpietrewicz.sp.ddd.sharedkernel.PositiveAmount;
import pl.mpietrewicz.sp.ddd.support.domain.DomainEventPublisher;
import pl.mpietrewicz.sp.modules.balance.domain.balance.operation.Operation;
import pl.mpietrewicz.sp.modules.balance.domain.balance.operation.type.AddPayment;
import pl.mpietrewicz.sp.modules.balance.domain.balance.operation.type.AddRefund;
import pl.mpietrewicz.sp.modules.balance.domain.balance.operation.type.CancelStopCalculating;
import pl.mpietrewicz.sp.modules.balance.domain.balance.operation.type.ChangePremium;
import pl.mpietrewicz.sp.modules.balance.domain.balance.operation.type.StartCalculating;
import pl.mpietrewicz.sp.modules.balance.domain.balance.operation.type.StopCalculating;
import pl.mpietrewicz.sp.modules.balance.domain.balance.publisherpolicy.PublishPolicy;
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

    public void stopCalculating(LocalDate end) {
        StopCalculating stopCalculating = new StopCalculating(end);
        commit(stopCalculating);
    }

    public void cancelStopCalculating() {
        try {
            StopCalculating stopCalculating = tryGetStopCalculating();
            CancelStopCalculating cancelStopCalculating = new CancelStopCalculating(stopCalculating);
            commit(cancelStopCalculating);
        } catch (UnavailabilityException e) {
            new CancelStopCalculating().handle(e, eventPublisher);
        }
    }

    private void commit(Operation operation) {
        PeriodProvider before = getLastOperation().getPeriod();

        PremiumSnapshot premiumSnapshot = premiumService.getPremiumSnapshot(contractId, operation.getRegistration());
        calculate(operation, premiumSnapshot);

        operations.add(operation);
        recalculateAfter(operation, premiumSnapshot);

        PeriodProvider after = getLastOperation().getPeriod();
        publishUpdatedBalanceResult(before, after);
    }

    private void calculate(Operation operation, PremiumSnapshot premiumSnapshot) {
        Operation previousOperation = getPreviousOperation(operation);
        operation.execute(previousOperation, premiumSnapshot, eventPublisher);
    }

    private void recalculateAfter(Operation operation, PremiumSnapshot premiumSnapshot) {
        try {
            tryRecalculateAfter(operation, premiumSnapshot);
        } catch (ReexecutionException e) {
            operation.handle(e, eventPublisher);
        }
    }

    private void tryRecalculateAfter(Operation operation, PremiumSnapshot premiumSnapshot) throws ReexecutionException {
        List<Operation> nextOperations = getNextOperationsAfter(operation);
        for (Operation nextOperation : nextOperations) {
            recalculate(nextOperation, premiumSnapshot);
        }
    }

    private void recalculate(Operation operation, PremiumSnapshot premiumSnapshot) throws ReexecutionException {
        Operation previousOperation = getPreviousOperation(operation);
        operation.reexecute(previousOperation, premiumSnapshot, eventPublisher);
    }

    private void publishUpdatedBalanceResult(PeriodProvider before, PeriodProvider after) {
        for (PublishPolicy publishPolicy : publishPolicies) {
            publishPolicy.doPublish(contractId, before, after);
        }
    }

    private StopCalculating tryGetStopCalculating() throws UnavailabilityException {
        try {
            return getCastedStopCalculating();
        } catch (NoSuchElementException e) {
            throw new UnavailabilityException("No valid stop calculating operation to register cancel operation ");
        }
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

    private Optional<Operation> getStopCalculating() {
        return getValidOperations().stream()
                .filter(StopCalculating.class::isInstance)
                .filter(operation -> ((StopCalculating) operation).isValid())
                .findAny();
    }

    private StopCalculating getCastedStopCalculating() {
        return getStopCalculating()
                .map(StopCalculating.class::cast)
                .orElseThrow();
    }

}