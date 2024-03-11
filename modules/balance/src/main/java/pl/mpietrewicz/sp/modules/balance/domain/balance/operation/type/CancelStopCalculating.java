package pl.mpietrewicz.sp.modules.balance.domain.balance.operation.type;

import lombok.Getter;
import pl.mpietrewicz.sp.ddd.canonicalmodel.events.CancelStopBalanceFailedEvent;
import pl.mpietrewicz.sp.ddd.canonicalmodel.publishedlanguage.snapshot.premium.PremiumSnapshot;
import pl.mpietrewicz.sp.ddd.support.domain.DomainEventPublisher;
import pl.mpietrewicz.sp.modules.balance.domain.balance.Period;
import pl.mpietrewicz.sp.modules.balance.domain.balance.operation.Operation;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static pl.mpietrewicz.sp.modules.balance.domain.balance.operation.OperationType.CANCEL_STOP_CALCULATING;

@Getter
public class CancelStopCalculating extends Operation {

    private StopCalculating stopCalculating;

    private LocalDate canceledEnd;

    private boolean valid;

    public CancelStopCalculating() {
        super(LocalDateTime.now());
        this.valid = false;
        this.type = CANCEL_STOP_CALCULATING;
    }

    public CancelStopCalculating(StopCalculating stopCalculating) {
        super(LocalDateTime.now());
        this.stopCalculating = stopCalculating; // todo: można przekazać tylko interfejs który pozlowli unieważnić tą operację
        this.canceledEnd = stopCalculating.getEnd();
        this.valid = true;
        this.type = CANCEL_STOP_CALCULATING;
    }

    public CancelStopCalculating(Long id, LocalDate canceledEnd, boolean valid, LocalDateTime registration, List<Period> periods) {
        super(id, registration.toLocalDate(), registration, periods);
        this.canceledEnd = canceledEnd;
        this.valid = valid;
        this.type = CANCEL_STOP_CALCULATING;
    }

    @Override
    public void execute(PremiumSnapshot premiumSnapshot, DomainEventPublisher eventPublisher) {
        stopCalculating.invalidate();
        invalidate();
    }

    @Override
    protected void reexecute(PremiumSnapshot premiumSnapshot, DomainEventPublisher eventPublisher) {
        throw new UnsupportedOperationException();
    }

    @Override
    public int orderComparator(Operation operation) {
        return stopCalculating.orderComparator(operation);
    }

    @Override
    protected Integer getPriority() {
        return stopCalculating.getPriority() - 1;
    }

    @Override
    protected void publishFailedEvent(Exception e, DomainEventPublisher eventPublisher) {
        CancelStopBalanceFailedEvent event = new CancelStopBalanceFailedEvent(e);
        eventPublisher.publish(event, "BalanceServiceImpl");
    }

    @Override
    public boolean isValid() {
        return valid;
    }

    public void invalidate() {
        this.valid = false;
    }

}