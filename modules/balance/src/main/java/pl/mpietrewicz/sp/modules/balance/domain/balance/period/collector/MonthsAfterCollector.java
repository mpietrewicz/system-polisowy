package pl.mpietrewicz.sp.modules.balance.domain.balance.period.collector;

import io.micrometer.core.annotation.Timed;
import pl.mpietrewicz.sp.ddd.annotations.domain.InternalDomainService;
import pl.mpietrewicz.sp.modules.balance.domain.balance.month.Month;
import pl.mpietrewicz.sp.modules.balance.domain.balance.operation.Operation;
import pl.mpietrewicz.sp.modules.balance.domain.balance.operation.type.StopCalculating;

import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@InternalDomainService
public class MonthsAfterCollector extends AllMonthsCollector {

    @Override
    @Timed(value = "MonthBeforeOperationCollector.getPeriodCopy")
    public List<Month> getMonthsFor(Operation operation, List<Operation> operations) {
        List<Month> result = new ArrayList<>();
        YearMonth monthBeforeOperation = getMonthBeforeOperation(operation);
        Month checked = null;

        outerLoop:
        for (Operation descendingOperation : getDescendingOperationsBefore(operation, operations)) {
            List<Month> descendingMonths = descendingOperation.getPeriodMonths().stream()
                    .sorted(Month::compareDescending)
                    .collect(Collectors.toList());
            for (Month month : descendingMonths) {
                if (month.isValid() && (checked == null || month.isBefore(checked))) {
                    if (month.getYearMonth().compareTo(monthBeforeOperation) >= 0) {
                        result.add(month);
                        checked = month;
                    } else {
                        break outerLoop;
                    }
                }
            }
        }

        return result;
    }

    private YearMonth getMonthBeforeOperation(Operation operation) {
        if (operation instanceof StopCalculating) {
            return YearMonth.from(((StopCalculating) operation).getEnd()).minusMonths(1);
        } else {
            return YearMonth.from(operation.getDate()).minusMonths(1);
        }
    }

}