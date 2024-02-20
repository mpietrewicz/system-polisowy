package pl.mpietrewicz.sp.modules.balance.domain.balance.operation.type;

import lombok.Getter;
import pl.mpietrewicz.sp.ddd.canonicalmodel.publishedlanguage.snapshot.premium.PremiumSnapshot;
import pl.mpietrewicz.sp.ddd.sharedkernel.Amount;
import pl.mpietrewicz.sp.ddd.support.domain.DomainEventPublisher;
import pl.mpietrewicz.sp.modules.balance.domain.balance.Period;
import pl.mpietrewicz.sp.modules.balance.domain.balance.operation.Operation;
import pl.mpietrewicz.sp.modules.balance.exceptions.ReexecutionException;

import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;

import static pl.mpietrewicz.sp.modules.balance.domain.balance.operation.OperationType.START_CALCULATING;

@Getter
public class StartCalculating extends Operation {

    private final Amount premium;

    public StartCalculating(YearMonth start, Amount premium) {
        super(start.atDay(1));
        this.type = START_CALCULATING;
        this.premium = premium;
    }

    public StartCalculating(Long id, YearMonth start, Amount premium, List<Period> periods) {
        super(id, start.atDay(1), periods);
        this.type = START_CALCULATING;
        this.premium = premium;
    }

    public void execute() {
        periods.add(new Period(date, new ArrayList<>(), true));
    }

    @Override
    public int orderComparator(Operation operation) {
        return -1;
    }

    @Override
    public void handle(ReexecutionException e, DomainEventPublisher eventPublisher) {
        throw new UnsupportedOperationException("Metoda nie obsługiwana w StartCalculating Operation");
    }

    @Override
    protected void execute(PremiumSnapshot premiumSnapshot, DomainEventPublisher eventPublisher) {
        throw new UnsupportedOperationException("Metoda nie obsługiwana w StartCalculating Operation");
    }

    @Override
    protected void reexecute(PremiumSnapshot premiumSnapshot, DomainEventPublisher eventPublisher) {
        throw new UnsupportedOperationException("Metoda nie obsługiwana w StartCalculating Operation");
    }

}