package pl.mpietrewicz.sp.modules.balance.domain.balance.period;

import lombok.RequiredArgsConstructor;
import pl.mpietrewicz.sp.ddd.annotations.domain.DomainFactory;
import pl.mpietrewicz.sp.modules.balance.domain.balance.month.Month;
import pl.mpietrewicz.sp.modules.balance.domain.balance.operation.Operation;
import pl.mpietrewicz.sp.modules.balance.domain.balance.period.collector.CollectorStrategyFactory;
import pl.mpietrewicz.sp.modules.balance.domain.balance.period.collector.MonthsCollector;

import java.time.LocalDate;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@DomainFactory
@RequiredArgsConstructor
public class PeriodFactory {

    private final CollectorStrategyFactory collectorStrategyFactory;

    public Period createFor(Operation operation, List<Operation> operations) {
        LocalDate start = getStartPeriod(operation, operations);
        MonthsCollector monthsCollector = collectorStrategyFactory.getFor(operation);
        List<Month> months = monthsCollector.getMonthsFor(operation, operations);

        return new Period(start, months);
    }

    public Period createForPaidTo(List<Operation> operations) {
        Operation lastOperation = getLastOperation(operations);
        LocalDate start = getStartPeriod(lastOperation, operations);
        MonthsCollector monthsCollector = collectorStrategyFactory.getForPaidTo();
        List<Month> months = monthsCollector.getMonthsFor(lastOperation, operations);

        return new Period(start, months);
    }

    private Operation getLastOperation(List<Operation> operations) {
        return operations.stream()
                .max(Operation::orderComparator)
                .orElseThrow();
    }

    protected LocalDate getStartPeriod(Operation operation, List<Operation> operations) {
        return getOperationsBefore(operation, operations).stream()
                .min(Operation::orderComparator)
                .map(Operation::getPeriodStart)
                .orElse(null);
    }

    protected List<Operation> getOperationsBefore(Operation operation, List<Operation> operations) {
        return operations.stream()
                .filter(isBefore(operation))
                .sorted(Operation::reverseOrderComparator)
                .collect(Collectors.toList());
    }

    private Predicate<Operation> isBefore(Operation operation) {
        return o -> o.isBefore(operation);
    }

}