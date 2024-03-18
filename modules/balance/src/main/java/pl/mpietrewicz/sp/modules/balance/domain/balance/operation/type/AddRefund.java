package pl.mpietrewicz.sp.modules.balance.domain.balance.operation.type;

import lombok.NoArgsConstructor;
import pl.mpietrewicz.sp.ddd.annotations.domain.ValueObject;
import pl.mpietrewicz.sp.ddd.canonicalmodel.events.AddRefundFailedEvent;
import pl.mpietrewicz.sp.ddd.canonicalmodel.publishedlanguage.AggregateId;
import pl.mpietrewicz.sp.ddd.sharedkernel.valueobject.Amount;
import pl.mpietrewicz.sp.modules.balance.domain.balance.Balance;
import pl.mpietrewicz.sp.modules.balance.domain.balance.Period;
import pl.mpietrewicz.sp.modules.balance.domain.balance.operation.Operation;
import pl.mpietrewicz.sp.modules.balance.domain.balance.policy.refund.RefundAmountPolicy;
import pl.mpietrewicz.sp.modules.balance.exceptions.ReexecutionException;
import pl.mpietrewicz.sp.modules.balance.exceptions.RefundException;

import javax.persistence.AttributeOverride;
import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.RollbackException;
import java.time.LocalDate;
import java.time.LocalDateTime;

import static pl.mpietrewicz.sp.modules.balance.domain.balance.operation.OperationType.ADD_REFUND;

@ValueObject
@Entity
@DiscriminatorValue("ADD_REFUND")
@NoArgsConstructor
public class AddRefund extends Operation {

    @Embedded
    @AttributeOverride(name = "aggregateId", column = @Column(name = "refundId", nullable = false))
    private AggregateId refundId;

    @Embedded
    @AttributeOverride(name = "value", column = @Column(name = "amount"))
    private Amount amount;

    public AddRefund(AggregateId refundId, LocalDate date, Amount amount, Balance balance) {
        super(date, LocalDateTime.now(), balance, ADD_REFUND);
        this.refundId = refundId;
        this.amount = amount;
    }

    @Override
    public void execute() {
        try {
            tryExecute();
        } catch (RefundException exception) {
            publishEvent(new AddRefundFailedEvent(refundId, amount, date, exception));
            throw new RollbackException(exception);
        }
    }

    @Override
    protected void reexecute(LocalDateTime registration) throws ReexecutionException {
        try {
            tryExecute();
        } catch (RefundException exception) {
            throw new ReexecutionException(exception, "Failed add refund {} during reexecution!", refundId.getId());
        }
    }

    @Override
    public void publishFailedEvent(ReexecutionException exception) {
        publishEvent(new AddRefundFailedEvent(refundId, amount, date, exception));
    }

    private void tryExecute() throws RefundException {
        RefundAmountPolicy refundAmountPolicy = new RefundAmountPolicy();
        Period period = getPeriod();
        refundAmountPolicy.refund(period, amount);
    }

}