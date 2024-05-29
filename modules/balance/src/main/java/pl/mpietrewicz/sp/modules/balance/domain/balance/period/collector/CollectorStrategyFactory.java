package pl.mpietrewicz.sp.modules.balance.domain.balance.period.collector;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import pl.mpietrewicz.sp.ddd.annotations.domain.InternalDomainService;
import pl.mpietrewicz.sp.modules.balance.domain.balance.operation.Operation;
import pl.mpietrewicz.sp.modules.balance.domain.balance.operation.RequiredPeriod;

@InternalDomainService
@RequiredArgsConstructor
public class CollectorStrategyFactory {

    @Qualifier("allMonthsCollector")
    private final MonthsCollector allMonthsCollector;

    @Qualifier("monthsAfterCollector")
    private final MonthsCollector monthsAfterCollector;

    @Qualifier("lastMonthCollector")
    private final MonthsCollector lastMonthCollector;

    @Qualifier("noMonthsCollector")
    private final MonthsCollector noMonthsCollector;

    public MonthsCollector getFor(Operation operation) {
        RequiredPeriod requiredPeriod = operation.getRequiredPeriod();

        switch (requiredPeriod) {
            case ALL_MONTHS:
                return allMonthsCollector;
            case MONTHS_AFTER:
                return monthsAfterCollector;
            case LAST_MONT:
                return lastMonthCollector;
            case NO_MONTHS:
                return noMonthsCollector;
            default:
                throw new IllegalArgumentException();
        }
    }

    public MonthsCollector getForPaidTo() {
        return lastMonthCollector;
    }

}