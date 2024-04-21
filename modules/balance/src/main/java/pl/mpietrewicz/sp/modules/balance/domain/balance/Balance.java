package pl.mpietrewicz.sp.modules.balance.domain.balance;

import lombok.NoArgsConstructor;
import pl.mpietrewicz.sp.ddd.annotations.domain.AggregateRoot;
import pl.mpietrewicz.sp.ddd.canonicalmodel.events.CancelStopBalanceFailedEvent;
import pl.mpietrewicz.sp.ddd.canonicalmodel.events.StopBalanceFailedEvent;
import pl.mpietrewicz.sp.ddd.canonicalmodel.publishedlanguage.AggregateId;
import pl.mpietrewicz.sp.ddd.canonicalmodel.publishedlanguage.PaymentPolicyEnum;
import pl.mpietrewicz.sp.ddd.canonicalmodel.publishedlanguage.snapshot.PaymentData;
import pl.mpietrewicz.sp.ddd.canonicalmodel.publishedlanguage.snapshot.RefundData;
import pl.mpietrewicz.sp.ddd.canonicalmodel.publishedlanguage.snapshot.premium.PremiumSnapshot;
import pl.mpietrewicz.sp.ddd.sharedkernel.Amount;
import pl.mpietrewicz.sp.ddd.support.domain.DomainEventPublisher;
import pl.mpietrewicz.sp.ddd.support.infrastructure.repo.BaseAggregateRoot;
import pl.mpietrewicz.sp.modules.balance.domain.balance.operation.Operation;
import pl.mpietrewicz.sp.modules.balance.domain.balance.operation.type.AddPayment;
import pl.mpietrewicz.sp.modules.balance.domain.balance.operation.type.AddRefund;
import pl.mpietrewicz.sp.modules.balance.domain.balance.operation.type.CancelStopCalculating;
import pl.mpietrewicz.sp.modules.balance.domain.balance.operation.type.ChangePremium;
import pl.mpietrewicz.sp.modules.balance.domain.balance.operation.type.StartCalculating;
import pl.mpietrewicz.sp.modules.balance.domain.balance.operation.type.StopCalculating;
import pl.mpietrewicz.sp.modules.balance.domain.balance.policy.publisher.PublishPolicy;
import pl.mpietrewicz.sp.modules.balance.exceptions.BalanceStoppedException;
import pl.mpietrewicz.sp.modules.balance.exceptions.NoStopCalculatingException;
import pl.mpietrewicz.sp.modules.balance.exceptions.ReexecutionException;
import pl.mpietrewicz.sp.modules.balance.exceptions.UnavailabilityException;
import pl.mpietrewicz.sp.modules.contract.application.api.PremiumService;

import javax.inject.Inject;
import javax.persistence.AttributeOverride;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.RollbackException;
import javax.persistence.Transient;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.stream.Collectors;

@AggregateRoot
@Entity
@NoArgsConstructor
public class Balance extends BaseAggregateRoot {

    @Inject
    @Transient
    protected DomainEventPublisher eventPublisher;

    @Inject
    @Transient
    protected PremiumService premiumService;

    @Inject
    @Transient
    protected List<PublishPolicy> publishPolicies;

    @Embedded
    @AttributeOverride(name = "aggregateId", column = @Column(name = "contractId", nullable = false))
    private AggregateId contractId;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "balance")
    protected List<Operation> operations;

    public Balance(AggregateId aggregateId, Long version, AggregateId contractId, List<Operation> operations) {
        this.aggregateId = aggregateId;
        this.version = version;
        this.contractId = contractId;
        this.operations = operations;
    }

    public Balance(AggregateId aggregateId, Long version, AggregateId contractId) {
        this.aggregateId = aggregateId;
        this.version = version;
        this.contractId = contractId;
        this.operations = new ArrayList<>();
    }

    public void addPayment(PaymentData paymentData, PaymentPolicyEnum paymentPolicyEnum) {
        AggregateId paymentId = paymentData.getAggregateId();
        LocalDate date = paymentData.getDate();
        Amount amount = paymentData.getAmount();

        AddPayment addPayment = new AddPayment(paymentId, date, amount, paymentPolicyEnum, this);
        commit(addPayment);
    }

    public void addRefund(RefundData refundData) {
        AggregateId refundId = refundData.getAggregateId();
        LocalDate date = refundData.getDate();
        Amount amount = refundData.getAmount();

        AddRefund addRefund = new AddRefund(refundId, date, amount, this);
        commit(addRefund);
    }

    public void changePremium(LocalDate date, LocalDateTime timestamp) {
        ChangePremium changePremium = new ChangePremium(date, timestamp, this);
        commit(changePremium);
    }

    public void stopCalculating(LocalDate end) {
        try {
            tryStopCalculating(end);
        } catch (BalanceStoppedException exception) {
            UnavailabilityException unavailabilityException = new UnavailabilityException(contractId, exception,
                    "Unable to stop calculating balance balance is currently stopped", contractId.getId());
            StopBalanceFailedEvent event = new StopBalanceFailedEvent(contractId, unavailabilityException);
            eventPublisher.publish(event, "BalanceServiceImpl");
            throw new RollbackException(unavailabilityException);
        }
    }

    public void cancelStopCalculating() {
        try {
            tryCancelStopCalculating();
        } catch (NoStopCalculatingException exception) {
            UnavailabilityException unavailabilityException = new UnavailabilityException(contractId, exception,
                    "Unable to cancel stop calculating balance balance isn't stop on contract {}", contractId.getId());
            CancelStopBalanceFailedEvent event = new CancelStopBalanceFailedEvent(contractId, unavailabilityException);
            eventPublisher.publish(event, "BalanceServiceImpl");
            throw new RollbackException(exception);
        }
    }

    public PremiumSnapshot getPremiumSnapshot(LocalDateTime timestamp) {
        return premiumService.getPremiumSnapshot(contractId, timestamp);
    }

    public void publishEvent(Serializable event) {
        eventPublisher.publish(event, "BalanceServiceImpl");
    }

    public LocalDate getStartBalance() {
        return getValidOperations().stream()
                .filter(StartCalculating.class::isInstance)
                .map(StartCalculating.class::cast)
                .map(StartCalculating::getDate)
                .findAny()
                .orElseThrow();
    }

    private void tryStopCalculating(LocalDate end) throws BalanceStoppedException {
        if (getStopCalculating().isPresent()) throw new BalanceStoppedException();
        StopCalculating stopCalculating = new StopCalculating(end, this);
        commit(stopCalculating);
    }

    private void tryCancelStopCalculating() throws NoStopCalculatingException {
        StopCalculating stopCalculating = tryGetStopCalculating();
        CancelStopCalculating cancelStopCalculating = new CancelStopCalculating(stopCalculating, this);
        commit(cancelStopCalculating);
    }

    private void commit(Operation operation) {
        PeriodProvider before = getLastOperation().getPeriod();

        Operation previousOperation = getPreviousOperation(operation);
        operation.execute(previousOperation.getPeriod());
        operations.add(operation);
        recalculateAfter(operation);

        PeriodProvider after = getLastOperation().getPeriod();
        publishUpdatedBalanceResult(before, after);
    }

    private void recalculateAfter(Operation operation) {
        try {
            tryRecalculateAfter(operation);
        } catch (ReexecutionException exception) {
            operation.publishFailedEvent(exception);
            throw new RollbackException(exception);
        }
    }

    private void tryRecalculateAfter(Operation operation) throws ReexecutionException {
        List<Operation> nextOperations = getNextOperationsAfter(operation);
        for (Operation nextOperation : nextOperations) {
            Operation previousOperation = getPreviousOperation(nextOperation);
            nextOperation.reexecute(previousOperation.getPeriod(), operation);
        }
    }

    private void publishUpdatedBalanceResult(PeriodProvider before, PeriodProvider after) {
        for (PublishPolicy publishPolicy : publishPolicies) {
            publishPolicy.doPublish(contractId, before, after);
        }
    }

    private StopCalculating tryGetStopCalculating() throws NoStopCalculatingException {
        return getStopCalculating()
                .orElseThrow(() -> new NoStopCalculatingException(new NoSuchElementException(),
                        "No stop calculating operation on contract {}", contractId.getId()));
    }

    private List<Operation> getValidOperations() {
        return operations.stream()
                .filter(Operation::isValid)
                .collect(Collectors.toList());
    }

    public Operation getPreviousOperation(Operation operation) {
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

}