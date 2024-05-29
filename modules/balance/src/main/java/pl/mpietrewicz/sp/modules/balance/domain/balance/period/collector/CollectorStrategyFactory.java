package pl.mpietrewicz.sp.modules.balance.domain.balance.period.collector;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import pl.mpietrewicz.sp.ddd.annotations.domain.InternalDomainService;
import pl.mpietrewicz.sp.modules.balance.domain.balance.operation.RequiredPeriod;

@InternalDomainService
@RequiredArgsConstructor
public class CollectorStrategyFactory {

    @Qualifier("allMonthsCollector")
    private final PeriodCollector allMonthsCollector;

    @Qualifier("lastMonthCollector")
    private final PeriodCollector lastMonthCollector;

    @Qualifier("noMonthsCollector")
    private final PeriodCollector noMonthsCollector;

    @Qualifier("disabledCollector")
    private final PeriodCollector disabledCollector;

    public PeriodCollector get(RequiredPeriod requiredPeriod) {
        switch (requiredPeriod) {
            case ALL_MONTHS:
                return allMonthsCollector;
            case LAST_MONT:
                return lastMonthCollector;
            case NO_MONTHS:
                return noMonthsCollector;
            case DISABLED:
                return disabledCollector;
            default:
                throw new IllegalArgumentException();
        }
    }

}