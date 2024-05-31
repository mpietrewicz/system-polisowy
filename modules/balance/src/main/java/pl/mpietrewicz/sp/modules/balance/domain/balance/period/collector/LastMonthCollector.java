package pl.mpietrewicz.sp.modules.balance.domain.balance.period.collector;

import io.micrometer.core.annotation.Timed;
import pl.mpietrewicz.sp.ddd.annotations.domain.InternalDomainService;
import pl.mpietrewicz.sp.modules.balance.domain.balance.month.Month;
import pl.mpietrewicz.sp.modules.balance.domain.balance.operation.Operation;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@InternalDomainService
public class LastMonthCollector extends AllMonthsCollector {

    @Override
    @Timed(value = "LastMonthCollector.getPeriodCopyFor")
    public List<Month> getMonthsFor(Operation operation, List<Operation> operations) {
        List<Month> result = new ArrayList<>();
        int addedOperations = 0;
        Month checked = null;

        outerLoop:
        for (Operation descendingOperation : getDescendingOperationsBefore(operation, operations)) {
            List<Month> descendingMonths = descendingOperation.getPeriodMonths().stream()
                    .sorted(Month::compareDescending)
                    .collect(Collectors.toList());
            for (Month month : descendingMonths) {
                if (month.isValid() && (checked == null || month.isBefore(checked))) {
                    if (addedOperations < 2) {
                        result.add(month);
                        addedOperations = addedOperations + 1;
                        checked = month;
                    } else {
                        break outerLoop;
                    }
                }
            }
        }

        return result;
    }

}