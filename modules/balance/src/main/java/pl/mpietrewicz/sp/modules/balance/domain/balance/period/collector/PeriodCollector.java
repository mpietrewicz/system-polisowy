package pl.mpietrewicz.sp.modules.balance.domain.balance.period.collector;

import pl.mpietrewicz.sp.modules.balance.domain.balance.operation.Operation;
import pl.mpietrewicz.sp.modules.balance.domain.balance.period.PartialPeriod;
import pl.mpietrewicz.sp.modules.balance.domain.balance.period.Period;

import java.util.List;

public interface PeriodCollector {

    Period getPeriodCopyFor(Operation operation, List<Operation> operations);

    PartialPeriod getPartialPeriodToSave(Period previousPeriod, Period currentPeriod);

}