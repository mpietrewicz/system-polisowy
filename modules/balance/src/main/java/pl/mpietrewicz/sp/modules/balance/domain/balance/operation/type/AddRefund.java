package pl.mpietrewicz.sp.modules.balance.domain.balance.operation.type;

import lombok.NoArgsConstructor;
import pl.mpietrewicz.sp.ddd.annotations.domain.ValueObject;
import pl.mpietrewicz.sp.ddd.canonicalmodel.events.AddRefundFailedEvent;
import pl.mpietrewicz.sp.ddd.canonicalmodel.publishedlanguage.AggregateId;
import pl.mpietrewicz.sp.ddd.sharedkernel.valueobject.PositiveAmount;
import pl.mpietrewicz.sp.modules.balance.domain.balance.Balance;
import pl.mpietrewicz.sp.modules.balance.domain.balance.operation.Operation;
import pl.mpietrewicz.sp.modules.balance.domain.balance.operation.RequiredPeriod;
import pl.mpietrewicz.sp.modules.balance.domain.balance.period.Period;
import pl.mpietrewicz.sp.modules.balance.domain.balance.policy.refund.RefundAmountPolicy;
import pl.mpietrewicz.sp.modules.balance.exceptions.ReexecutionException;
import pl.mpietrewicz.sp.modules.balance.exceptions.RefundException;

import javax.persistence.AttributeOverride;
import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.RollbackException;
import javax.persistence.Transient;
import java.time.LocalDate;
import java.time.LocalDateTime;

import static pl.mpietrewicz.sp.modules.balance.domain.balance.operation.OperationType.ADD_REFUND;
import static pl.mpietrewicz.sp.modules.balance.domain.balance.operation.RequiredPeriod.ALL_MONTHS;

@ValueObject
@Entity
@DiscriminatorValue("ADD_REFUND")
@NoArgsConstructor
public class AddRefund extends Operation {

    @Embedded
    @AttributeOverride(name = "aggregateId", column = @Column(name = "refundId", nullable = false))
    private AggregateId refundId;

    @Embedded
    @AttributeOverride(name = "value", column = @Column(name = "refund"))
    private PositiveAmount refund;

    @Transient
    private static final RequiredPeriod requiredPeriod = ALL_MONTHS;

    public AddRefund(AggregateId refundId, LocalDate date, PositiveAmount refund, Balance balance) {
        super(date, LocalDateTime.now(), balance, ADD_REFUND);
        this.refundId = refundId;
        this.refund = refund;
    }

    @Override
    public void execute(Period period) {
        try {
            tryExecute(period);
        } catch (RefundException exception) {
            publishEvent(new AddRefundFailedEvent(refundId, refund, date, exception));
            throw new RollbackException(exception);
        }
    }

    @Override
    protected void reexecute(Period period, LocalDateTime registration) throws ReexecutionException {
        try {
            tryExecute(period);
        } catch (RefundException exception) {
            throw new ReexecutionException(exception, "Failed add refund {} during reexecution!", refundId.getId());
        }
    }

    @Override
    public RequiredPeriod getRequiredPeriod() {
        return requiredPeriod;
    }

    @Override
    public void publishFailedEvent(ReexecutionException exception) {
        publishEvent(new AddRefundFailedEvent(refundId, refund, date, exception));
    }

    private void tryExecute(Period period) throws RefundException {
        RefundAmountPolicy refundAmountPolicy = new RefundAmountPolicy();
        refundAmountPolicy.refund(period, refund);
    }

}