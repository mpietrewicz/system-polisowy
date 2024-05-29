package pl.mpietrewicz.sp.modules.balance.domain.balance.period.collector;

import pl.mpietrewicz.sp.modules.balance.domain.balance.month.Month;
import pl.mpietrewicz.sp.modules.balance.domain.balance.operation.Operation;

import java.util.List;

public interface MonthsCollector {

    List<Month> getMonthsFor(Operation operation, List<Operation> operations);

}