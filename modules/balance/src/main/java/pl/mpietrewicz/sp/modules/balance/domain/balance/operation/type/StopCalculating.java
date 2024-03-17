package pl.mpietrewicz.sp.modules.balance.domain.balance.operation.type;

import lombok.Getter;
import pl.mpietrewicz.sp.ddd.canonicalmodel.events.StopBalanceFailedEvent;
import pl.mpietrewicz.sp.ddd.canonicalmodel.publishedlanguage.AggregateId;
import pl.mpietrewicz.sp.ddd.sharedkernel.Amount;
import pl.mpietrewicz.sp.ddd.support.domain.DomainEventPublisher;
import pl.mpietrewicz.sp.modules.balance.domain.balance.Period;
import pl.mpietrewicz.sp.modules.balance.domain.balance.operation.Operation;
import pl.mpietrewicz.sp.modules.balance.domain.balance.operation.OperationType;
import pl.mpietrewicz.sp.modules.balance.exceptions.BalanceException;
import pl.mpietrewicz.sp.modules.balance.exceptions.UnavailabilityException;

import javax.persistence.RollbackException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.List;

import static pl.mpietrewicz.sp.modules.balance.domain.balance.operation.OperationType.STOP_CALCULATING;

@Getter
public class StopCalculating extends Operation implements StopCalculatingService {

    private static final OperationType operationType = STOP_CALCULATING;

    private Amount excess;

    private final LocalDate end;

    private boolean valid;

    public StopCalculating(LocalDate end, DomainEventPublisher eventPublisher) {
        super(LocalDateTime.now(), eventPublisher);
        this.end = end;
        this.valid = true;
    }

    public StopCalculating(Long id, LocalDate end, LocalDateTime registration, Amount excess, boolean valid, List<Period> periods) {
        super(id, registration.toLocalDate(), registration, periods);
        this.end = end;
        this.excess = excess;
        this.valid = valid;
    }

    @Override
    public void execute(AggregateId contractId) {
        YearMonth monthOfEnd = YearMonth.from(end);
        this.excess = getPeriod().tryRefundUpTo(monthOfEnd);
    }

    @Override
    protected void reexecute(AggregateId contractId, LocalDateTime registration) {
        if (isValid()) {
            execute(contractId);
        } else {
            throw new UnsupportedOperationException();
        }
    }

    @Override
    public int orderComparator(Operation operation) {
        return orderAlwaysLast(operation);
    }

    @Override
    public Integer getPriority() {
        return 30;
    }

    @Override
    protected void publishFailedEvent(AggregateId contractId, BalanceException e) {
        throw new UnsupportedOperationException();
    }

    public static void handle(UnavailabilityException e, DomainEventPublisher eventPublisher) {
        StopBalanceFailedEvent event = new StopBalanceFailedEvent(e.getContractId(), e);
        eventPublisher.publish(event, "BalanceServiceImpl");
        throw new RollbackException(e);
    }

    @Override
    public OperationType getOperationType() {
        return operationType;
    }

    @Override
    public boolean isValid() {
        return valid;
    }

    @Override
    public void invalidate() {
        this.valid = false;
    }

    @Override
    public LocalDate getEnd() {
        return end;
    }

    private int orderAlwaysLast(Operation operation) {
        return this == operation
                ? 0
                : 1;
    }

}