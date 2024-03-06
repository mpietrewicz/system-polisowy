package pl.mpietrewicz.sp.modules.balance.domain.balance.operation.type;

import lombok.Getter;
import pl.mpietrewicz.sp.ddd.canonicalmodel.publishedlanguage.snapshot.premium.PremiumSnapshot;
import pl.mpietrewicz.sp.ddd.sharedkernel.Amount;
import pl.mpietrewicz.sp.ddd.support.domain.DomainEventPublisher;
import pl.mpietrewicz.sp.modules.balance.domain.balance.Period;
import pl.mpietrewicz.sp.modules.balance.domain.balance.operation.Operation;

import java.time.YearMonth;
import java.util.List;

import static pl.mpietrewicz.sp.modules.balance.domain.balance.operation.OperationType.START_CALCULATING;

@Getter
public class StartCalculating extends Operation {

    private final Amount premium;

    public StartCalculating(YearMonth start, Amount premium, Period period) {
        super(start.atDay(1));
        this.type = START_CALCULATING;
        this.premium = premium;
        this.periods.add(period);
    }

    public StartCalculating(Long id, YearMonth start, Amount premium, List<Period> periods) {
        super(id, start.atDay(1), periods);
        this.type = START_CALCULATING;
        this.premium = premium;
    }

    @Override
    protected void execute(PremiumSnapshot premiumSnapshot, DomainEventPublisher eventPublisher) {
        throw new UnsupportedOperationException();
    }

    @Override
    protected void reexecute(PremiumSnapshot premiumSnapshot, DomainEventPublisher eventPublisher) {
        throw new UnsupportedOperationException();
    }

    @Override
    protected void publishFailedEvent(Exception e, DomainEventPublisher eventPublisher) {
        throw new UnsupportedOperationException();
    }

    @Override
    public int orderComparator(Operation operation) {
        return this == operation
                ? 0
                : -1;
    }

}