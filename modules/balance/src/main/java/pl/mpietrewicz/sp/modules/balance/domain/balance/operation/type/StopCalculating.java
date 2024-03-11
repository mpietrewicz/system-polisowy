package pl.mpietrewicz.sp.modules.balance.domain.balance.operation.type;

import lombok.Getter;
import pl.mpietrewicz.sp.ddd.canonicalmodel.events.StopBalanceFailedEvent;
import pl.mpietrewicz.sp.ddd.canonicalmodel.publishedlanguage.snapshot.premium.PremiumSnapshot;
import pl.mpietrewicz.sp.ddd.sharedkernel.Amount;
import pl.mpietrewicz.sp.ddd.support.domain.DomainEventPublisher;
import pl.mpietrewicz.sp.modules.balance.domain.balance.Period;
import pl.mpietrewicz.sp.modules.balance.domain.balance.operation.Operation;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.List;

import static pl.mpietrewicz.sp.modules.balance.domain.balance.operation.OperationType.STOP_CALCULATING;

@Getter
public class StopCalculating extends Operation {

    private Amount excess;

    private final LocalDate end;

    private boolean valid;

    public StopCalculating(LocalDate end) {
        super(LocalDateTime.now());
        this.end = end;
        this.valid = true;
        this.type = STOP_CALCULATING;
    }

    public StopCalculating(Long id, LocalDate end, boolean valid, LocalDateTime registration, Amount excess, List<Period> periods) {
        super(id, registration.toLocalDate(), registration, periods);
        this.end = end;
        this.valid = valid;
        this.excess = excess;
        this.type = STOP_CALCULATING;
    }

    @Override
    public void execute(PremiumSnapshot premiumSnapshot, DomainEventPublisher eventPublisher) {
        YearMonth monthOfEnd = YearMonth.from(end);
        this.excess = getPeriod().tryRefundUpTo(monthOfEnd);
        // todo: albo liczę ile jeszcze okresów mam niedopłaty
    }

    @Override
    protected void reexecute(PremiumSnapshot premiumSnapshot, DomainEventPublisher eventPublisher) {
        if (isValid()) {
            execute(premiumSnapshot, eventPublisher);
        } else {
            throw new UnsupportedOperationException();
        }
    }

    @Override
    public int orderComparator(Operation operation) {
        return orderAlwaysLast(operation);
    }

    @Override
    protected Integer getPriority() {
        return 30;
    }

    @Override
    protected void publishFailedEvent(Exception e, DomainEventPublisher eventPublisher) {
        StopBalanceFailedEvent event = new StopBalanceFailedEvent(end, e);
        eventPublisher.publish(event, "BalanceServiceImpl");
    }

    @Override
    public boolean isValid() {
        return valid;
    }

    public void invalidate() {
        this.valid = false;
    }

    private int orderAlwaysLast(Operation operation) {
        return this == operation
                ? 0
                : 1;
    }

}