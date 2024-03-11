package pl.mpietrewicz.sp.modules.balance.domain.balance.operation.type;

import lombok.Getter;
import pl.mpietrewicz.sp.ddd.canonicalmodel.events.AddRefundFailedEvent;
import pl.mpietrewicz.sp.ddd.canonicalmodel.publishedlanguage.AggregateId;
import pl.mpietrewicz.sp.ddd.sharedkernel.Amount;
import pl.mpietrewicz.sp.ddd.support.domain.DomainEventPublisher;
import pl.mpietrewicz.sp.modules.balance.domain.balance.Period;
import pl.mpietrewicz.sp.modules.balance.domain.balance.operation.Operation;
import pl.mpietrewicz.sp.modules.balance.domain.balance.operation.OperationType;
import pl.mpietrewicz.sp.modules.balance.exceptions.ReexecutionException;
import pl.mpietrewicz.sp.modules.balance.exceptions.RefundException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static pl.mpietrewicz.sp.modules.balance.domain.balance.operation.OperationType.ADD_REFUND;

@Getter
public class AddRefund extends Operation {

    private static final OperationType operationType = ADD_REFUND;

    private final Amount refund;

    public AddRefund(LocalDate date, Amount refund, DomainEventPublisher eventPublisher) {
        super(date, eventPublisher);
        this.refund = refund;
    }

    public AddRefund(Long id, LocalDate date, LocalDateTime registration, Amount refund, List<Period> periods) {
        super(id, date, registration, periods);
        this.refund = refund;
    }

    @Override
    public void execute(AggregateId contractId) {
        try {
            tryExecute();
        } catch (RefundException e) {
            handle(e);
        }
    }

    @Override
    protected void reexecute(AggregateId contractId, LocalDateTime registration) throws ReexecutionException {
        try {
            tryExecute();
        } catch (RefundException e) {
            throw new ReexecutionException("Add refund failed!", e);
        }
    }

    @Override
    protected void publishFailedEvent(Exception e) {
        AddRefundFailedEvent event = new AddRefundFailedEvent(refund, date, e);
        eventPublisher.publish(event, "BalanceServiceImpl");
    }

    @Override
    public OperationType getOperationType() {
        return operationType;
    }

    private void tryExecute() throws RefundException {
        getPeriod().tryRefund(refund);
    }

}