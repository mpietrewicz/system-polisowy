package pl.mpietrewicz.sp.modules.balance.domain.balance.period.collector;

import pl.mpietrewicz.sp.ddd.annotations.domain.InternalDomainService;
import pl.mpietrewicz.sp.modules.balance.domain.balance.month.Month;
import pl.mpietrewicz.sp.modules.balance.domain.balance.operation.Operation;

import java.util.ArrayList;
import java.util.List;

@InternalDomainService
public class NoMonthsCollector implements MonthsCollector {

    @Override
    public List<Month> getMonthsFor(Operation operation, List<Operation> operations) {
        return new ArrayList<>();
    }

}