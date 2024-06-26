package pl.mpietrewicz.sp.modules.balance.domain.balance.period.collector;

import io.micrometer.core.annotation.Timed;
import pl.mpietrewicz.sp.ddd.annotations.domain.InternalDomainService;
import pl.mpietrewicz.sp.modules.balance.domain.balance.month.Month;
import pl.mpietrewicz.sp.modules.balance.domain.balance.operation.Operation;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@InternalDomainService
public class AllMonthsCollector implements MonthsCollector {

    @Override
    @Timed(value = "AllMonthsCollector.getPeriodCopyFor")
    public List<Month> getMonthsFor(Operation operation, List<Operation> operations) {
        List<Month> result = new ArrayList<>();
        Month checked = null;
        for (Operation descendingOperation : getDescendingOperationsBefore(operation, operations)) {
            List<Month> descendingMonths = descendingOperation.getPeriodMonths().stream()
                    .sorted(Month::compareDescending)
                    .collect(Collectors.toList());
            for (Month month : descendingMonths) {
                if (month.isValid() && (checked == null || month.isBefore(checked))) {
                    result.add(month);
                    checked = month;
                }
            }
        }

        return result;
    }

    protected LocalDate getStartPeriod(Operation operation, List<Operation> operations) {
        return getDescendingOperationsBefore(operation, operations).stream()
                .min(Operation::orderComparator)
                .map(Operation::getPeriodStart)
                .orElse(null);
    }

    protected List<Operation> getDescendingOperationsBefore(Operation operation, List<Operation> operations) {
        return operations.stream()
                .filter(isBefore(operation))
                .sorted(Operation::reverseOrderComparator)
                .collect(Collectors.toList());
    }

    private Predicate<Operation> isBefore(Operation operation) {
        return o -> o.isBefore(operation);
    }

}