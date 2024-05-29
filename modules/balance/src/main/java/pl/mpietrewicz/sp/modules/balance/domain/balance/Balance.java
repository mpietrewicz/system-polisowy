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
import pl.mpietrewicz.sp.ddd.sharedkernel.valueobject.PositiveAmount;
import pl.mpietrewicz.sp.ddd.support.domain.DomainEventPublisher;
import pl.mpietrewicz.sp.ddd.support.infrastructure.repo.BaseAggregateRoot;
import pl.mpietrewicz.sp.modules.balance.domain.balance.operation.Operation;
import pl.mpietrewicz.sp.modules.balance.domain.balance.operation.type.AddPayment;
import pl.mpietrewicz.sp.modules.balance.domain.balance.operation.type.AddRefund;
import pl.mpietrewicz.sp.modules.balance.domain.balance.operation.type.CancelStopCalculating;
import pl.mpietrewicz.sp.modules.balance.domain.balance.operation.type.ChangePremium;
import pl.mpietrewicz.sp.modules.balance.domain.balance.operation.type.StopCalculating;
import pl.mpietrewicz.sp.modules.balance.domain.balance.period.PartialPeriod;
import pl.mpietrewicz.sp.modules.balance.domain.balance.period.Period;
import pl.mpietrewicz.sp.modules.balance.domain.balance.period.PeriodFactory;
import pl.mpietrewicz.sp.modules.balance.domain.balance.period.PeriodProvider;
import pl.mpietrewicz.sp.modules.balance.domain.balance.period.collector.PartialPeriodCollector;
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
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.stream.Collectors;

@AggregateRoot
@Entity
@NoArgsConstructor
public class Balance extends BaseAggregateRoot {

    @Embedded
    @AttributeOverride(name = "aggregateId", column = @Column(name = "contractId", nullable = false))
    private AggregateId contractId;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "balance")
    protected List<Operation> operations;

    @Inject
    @Transient
    private DomainEventPublisher eventPublisher;

    @Inject
    @Transient
    private PremiumService premiumService;

    @Inject
    @Transient
    private List<PublishPolicy> publishPolicies;

    @Inject
    @Transient
    private PeriodFactory periodFactory;

    @Inject
    @Transient
    private PartialPeriodCollector partialPeriodCollector;

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
        PositiveAmount payment = paymentData.getPayment();

        AddPayment addPayment = new AddPayment(paymentId, date, payment, paymentPolicyEnum, this);
        commit(addPayment);
    }

    public void addRefund(RefundData refundData) {
        AggregateId refundId = refundData.getAggregateId();
        LocalDate date = refundData.getDate();
        PositiveAmount refund = refundData.getRefund();

        AddRefund addRefund = new AddRefund(refundId, date, refund, this);
        commit(addRefund);
    }

    public RefundData addRefund(PositiveAmount refund) {
        AggregateId refundId = AggregateId.generate();
        LocalDate date = LocalDate.now();

        AddRefund addRefund = new AddRefund(refundId, date, refund, this);
        commit(addRefund);
        return new RefundData(refundId, contractId, date, refund);
    }

    public void changePremium(LocalDate date, LocalDateTime timestamp) {
        ChangePremium changePremium = new ChangePremium(date, timestamp, this);
        commit(changePremium);
    }

    public void stopCalculating(LocalDate end) {
        try {
            tryStopCalculating(end);
        } catch (BalanceStoppedException balanceStoppedException) {
            handle(balanceStoppedException);
        }
    }

    public void cancelStopCalculating() {
        try {
            tryCancelStopCalculating();
        } catch (NoStopCalculatingException noStopCalculatingException) {
            handle(noStopCalculatingException);
        }
    }

    public PremiumSnapshot getPremiumSnapshot(LocalDateTime timestamp) {
        return premiumService.getPremiumSnapshot(contractId, timestamp);
    }

    public void publishEvent(Serializable event) {
        eventPublisher.publish(event);
    }

    public Map<YearMonth, BigDecimal> getPaidTo() {
        Operation lastOperation = getLastOperation();
        Period period = periodFactory.createForPaidTo(getValidOperations());
        YearMonth lastPaidYearMonth = period.getLastPaidYearMonth();

        if (lastOperation instanceof StopCalculating) {
            return Map.of(lastPaidYearMonth, ((StopCalculating) lastOperation).getExcess().getValue());
        } else {
            return Map.of(lastPaidYearMonth, period.getExcess());
        }
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
        calculate(operation);
        operations.add(operation);
        recalculateAfter(operation);
    }

    private void calculate(Operation operation) {
        Period periodBefore = periodFactory.createFor(operation, getValidOperations());
        Period periodAfter = operation.executeOn(periodBefore);
        PartialPeriod partialPeriod = partialPeriodCollector.getPartialPeriod(periodBefore, periodAfter);
        operation.savePeriod(partialPeriod);
        publishUpdatedBalanceResult(partialPeriod);
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

        for (Operation toRecalculate : nextOperations) {
            Period periodBefore = periodFactory.createFor(toRecalculate, getValidOperations());
            Period periodAfter = toRecalculate.reexecuteOn(periodBefore, operation);
            PartialPeriod partialPeriod = partialPeriodCollector.getPartialPeriod(periodBefore, periodAfter);
            toRecalculate.savePeriod(partialPeriod);
            publishUpdatedBalanceResult(partialPeriod);
        }
    }

    private Operation getLastOperation() {
        return getValidOperations().stream()
                .max(Operation::orderComparator)
                .orElseThrow();
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

    private List<Operation> getNextOperationsAfter(Operation operation) {
        return getValidOperations().stream()
                .filter(o -> o.isAfter(operation))
                .sorted(Operation::orderComparator)
                .collect(Collectors.toList());
    }

    private Optional<StopCalculating> getStopCalculating() {
        return getValidOperations().stream()
                .filter(StopCalculating.class::isInstance)
                .map(StopCalculating.class::cast)
                .findAny();
    }

    private void publishUpdatedBalanceResult(PeriodProvider periodProvider) {
        for (PublishPolicy publishPolicy : publishPolicies) {
            publishPolicy.doPublish(contractId, periodProvider);
        }
    }

    private void handle(BalanceStoppedException exception) {
        UnavailabilityException unavailabilityException = new UnavailabilityException(contractId, exception,
                "Unable to stop calculating balance balance is currently stopped", contractId.getId());
        StopBalanceFailedEvent event = new StopBalanceFailedEvent(contractId, unavailabilityException);
        eventPublisher.publish(event);
        throw new RollbackException(unavailabilityException);
    }

    private void handle(NoStopCalculatingException exception) {
        UnavailabilityException unavailabilityException = new UnavailabilityException(contractId, exception,
                "Unable to cancel stop calculating balance balance isn't stop on contract {}", contractId.getId());
        CancelStopBalanceFailedEvent event = new CancelStopBalanceFailedEvent(contractId, unavailabilityException);
        eventPublisher.publish(event);
        throw new RollbackException(exception);
    }

}