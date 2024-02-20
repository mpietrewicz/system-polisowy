package pl.mpietrewicz.sp.modules.balance.domain.balance.operation.type;

import lombok.NoArgsConstructor;
import pl.mpietrewicz.sp.ddd.annotations.domain.ValueObject;
import pl.mpietrewicz.sp.ddd.canonicalmodel.events.AddRefundFailedEvent;
import pl.mpietrewicz.sp.ddd.canonicalmodel.publishedlanguage.snapshot.premium.PremiumSnapshot;
import pl.mpietrewicz.sp.ddd.sharedkernel.Amount;
import pl.mpietrewicz.sp.ddd.support.domain.DomainEventPublisher;
import pl.mpietrewicz.sp.modules.balance.domain.balance.operation.Operation;
import pl.mpietrewicz.sp.modules.balance.exceptions.ReexecutionException;
import pl.mpietrewicz.sp.modules.balance.exceptions.RefundException;

import javax.persistence.AttributeOverride;
import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.RollbackException;
import java.time.LocalDate;

import static pl.mpietrewicz.sp.modules.balance.domain.balance.operation.OperationType.ADD_REFUND;

@ValueObject
@Entity
@DiscriminatorValue("ADD_REFUND")
@NoArgsConstructor
public class AddRefund extends Operation {

    @Embedded
    @AttributeOverride(name = "value", column = @Column(name = "amount"))
    private Amount refund;

    public AddRefund(LocalDate date, Amount refund) {
        super(date);
        this.refund = refund;
        this.type = ADD_REFUND;
    }

    @Override
    public void execute(PremiumSnapshot premiumSnapshot, DomainEventPublisher eventPublisher) {
        try {
            tryExecute();
        } catch (RefundException e) {
            publishFailedEvent(e, eventPublisher);
            throw new RollbackException(e);
        }
    }


    @Override
    protected void reexecute(PremiumSnapshot premiumSnapshot, DomainEventPublisher eventPublisher)
            throws ReexecutionException {
        try {
            tryExecute();
        } catch (RefundException e) {
            throw new ReexecutionException("Add refund failed!", e);
        }
    }

    @Override
    public void handle(ReexecutionException e, DomainEventPublisher eventPublisher) {
        publishFailedEvent(e, eventPublisher);
    }

    private void tryExecute() throws RefundException {
        getCurrentPeriod().tryRefund(refund);
    }

    private void publishFailedEvent(Exception e, DomainEventPublisher eventPublisher) {
        AddRefundFailedEvent event = new AddRefundFailedEvent(refund, date, e);
        eventPublisher.publish(event, "BalanceServiceImpl");
    }

}