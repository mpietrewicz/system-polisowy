package pl.mpietrewicz.sp.modules.balance.domain.balance.period.collector;

import lombok.RequiredArgsConstructor;
import pl.mpietrewicz.sp.ddd.annotations.domain.InternalDomainService;
import pl.mpietrewicz.sp.modules.balance.domain.balance.month.Month;
import pl.mpietrewicz.sp.modules.balance.domain.balance.operation.Operation;
import pl.mpietrewicz.sp.modules.balance.domain.balance.period.PartialPeriod;
import pl.mpietrewicz.sp.modules.balance.domain.balance.period.Period;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@InternalDomainService
@RequiredArgsConstructor
public class DisabledCollector implements PeriodCollector {

    @Override
    public Period getPeriodCopyFor(Operation operation, List<Operation> operations) {
        LocalDate start = operation.getPeriodStart();
        List<Month> months = operation.getPeriodMonths().stream()
                .map(Month::createCopy)
                .collect(Collectors.toList());
        return new Period(start, months, "period from AllPeriodCollector");
    }

    @Override
    public PartialPeriod getPartialPeriodToSave(Period previousPeriod, Period currentPeriod) {
        return new PartialPeriod(currentPeriod.getStart(), currentPeriod.getMonths(), true, currentPeriod.getInfo());
    }

}