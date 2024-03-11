package pl.mpietrewicz.sp.modules.balance.domain.balance.operation.type;

import lombok.Getter;
import pl.mpietrewicz.sp.ddd.canonicalmodel.events.AddRefundFailedEvent;
import pl.mpietrewicz.sp.ddd.canonicalmodel.publishedlanguage.snapshot.premium.PremiumSnapshot;
import pl.mpietrewicz.sp.ddd.sharedkernel.Amount;
import pl.mpietrewicz.sp.ddd.support.domain.DomainEventPublisher;
import pl.mpietrewicz.sp.modules.balance.domain.balance.Period;
import pl.mpietrewicz.sp.modules.balance.domain.balance.operation.Operation;
import pl.mpietrewicz.sp.modules.balance.exceptions.ReexecutionException;
import pl.mpietrewicz.sp.modules.balance.exceptions.RefundException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static pl.mpietrewicz.sp.modules.balance.domain.balance.operation.OperationType.ADD_REFUND;

@Getter
public class AddRefund extends Operation {

    private final Amount refund;

    public AddRefund(LocalDate date, Amount refund) {
        super(date);
        this.refund = refund;
        this.type = ADD_REFUND;
    }

    public AddRefund(Long id, LocalDate date, LocalDateTime registration, Amount refund, List<Period> periods) {
        super(id, date, registration, periods);
        this.refund = refund;
        this.type = ADD_REFUND;
    }

    @Override
    public void execute(PremiumSnapshot premiumSnapshot, DomainEventPublisher eventPublisher) {
        try {
            tryExecute();
        } catch (RefundException e) {
            handle(e, eventPublisher);
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
    protected void publishFailedEvent(Exception e, DomainEventPublisher eventPublisher) {
        AddRefundFailedEvent event = new AddRefundFailedEvent(refund, date, e);
        eventPublisher.publish(event, "BalanceServiceImpl");
    }

    private void tryExecute() throws RefundException {
        getPeriod().tryRefund(refund);
    }

}