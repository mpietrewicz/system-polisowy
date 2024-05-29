package pl.mpietrewicz.sp.modules.balance.domain.balance.period.collector;

import lombok.RequiredArgsConstructor;
import pl.mpietrewicz.sp.ddd.annotations.domain.InternalDomainService;
import pl.mpietrewicz.sp.modules.balance.domain.balance.operation.Operation;
import pl.mpietrewicz.sp.modules.balance.domain.balance.period.PartialPeriod;
import pl.mpietrewicz.sp.modules.balance.domain.balance.period.Period;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@InternalDomainService
@RequiredArgsConstructor
public class NoMonthsCollector implements PeriodCollector {

    @Override
    public Period getPeriodCopyFor(Operation operation, List<Operation> operations) {
        LocalDate start = operation.getPeriodStart();
        return new Period(start, new ArrayList<>(), "period from NoMonthsPeriodCollector");
    }

    @Override
    public PartialPeriod getPartialPeriodToSave(Period previousPeriod, Period currentPeriod) {
        return new PartialPeriod(currentPeriod.getStart(), new ArrayList<>(), true, currentPeriod.getInfo());
    }

}