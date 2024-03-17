package pl.mpietrewicz.sp.modules.balance.domain.balance.operation.type;

import lombok.Getter;
import pl.mpietrewicz.sp.ddd.canonicalmodel.events.CancelStopBalanceFailedEvent;
import pl.mpietrewicz.sp.ddd.canonicalmodel.publishedlanguage.AggregateId;
import pl.mpietrewicz.sp.ddd.support.domain.DomainEventPublisher;
import pl.mpietrewicz.sp.modules.balance.domain.balance.Period;
import pl.mpietrewicz.sp.modules.balance.domain.balance.operation.Operation;
import pl.mpietrewicz.sp.modules.balance.domain.balance.operation.OperationType;
import pl.mpietrewicz.sp.modules.balance.exceptions.BalanceException;
import pl.mpietrewicz.sp.modules.balance.exceptions.UnavailabilityException;

import javax.persistence.RollbackException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static pl.mpietrewicz.sp.modules.balance.domain.balance.operation.OperationType.CANCEL_STOP_CALCULATING;

@Getter
public class CancelStopCalculating extends Operation {

    private static final OperationType operationType = CANCEL_STOP_CALCULATING;

    private StopCalculatingService stopCalculatingService;

    private LocalDate canceledEnd;

    private boolean valid;

    public CancelStopCalculating(DomainEventPublisher eventPublisher) {
        super(LocalDateTime.now(), eventPublisher);
        this.valid = false;
    }

    public CancelStopCalculating(StopCalculatingService stopCalculatingService, DomainEventPublisher eventPublisher) {
        super(LocalDateTime.now(), eventPublisher);
        this.stopCalculatingService = stopCalculatingService;
        this.canceledEnd = stopCalculatingService.getEnd();
        this.valid = true;
    }

    public CancelStopCalculating(Long id, LocalDate canceledEnd, LocalDateTime registration, boolean valid, List<Period> periods) {
        super(id, registration.toLocalDate(), registration, periods);
        this.canceledEnd = canceledEnd;
        this.valid = valid;
    }

    @Override
    public void execute(AggregateId contractId) {
        stopCalculatingService.invalidate();
        invalidate();
    }

    @Override
    protected void reexecute(AggregateId contractId, LocalDateTime registration) {
        throw new UnsupportedOperationException();
    }

    @Override
    protected void publishFailedEvent(AggregateId contractId, BalanceException e) {
        throw new UnsupportedOperationException();
    }

    public static void handle(UnavailabilityException e, DomainEventPublisher eventPublisher) {
        CancelStopBalanceFailedEvent event = new CancelStopBalanceFailedEvent(e.getContractId(), e);
        eventPublisher.publish(event, "BalanceServiceImpl");
        throw new RollbackException(e);
    }

    @Override
    public OperationType getOperationType() {
        return operationType;
    }

    @Override
    public int orderComparator(Operation operation) {
        return stopCalculatingService.orderComparator(operation);
    }

    @Override
    protected Integer getPriority() {
        return stopCalculatingService.getPriority() - 1;
    }

    @Override
    public boolean isValid() {
        return valid;
    }

    public void invalidate() {
        this.valid = false;
    }

}