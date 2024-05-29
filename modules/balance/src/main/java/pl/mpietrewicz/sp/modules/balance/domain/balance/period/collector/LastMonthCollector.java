package pl.mpietrewicz.sp.modules.balance.domain.balance.period.collector;

import lombok.RequiredArgsConstructor;
import pl.mpietrewicz.sp.ddd.annotations.domain.InternalDomainService;
import pl.mpietrewicz.sp.modules.balance.domain.balance.month.Month;
import pl.mpietrewicz.sp.modules.balance.domain.balance.operation.Operation;
import pl.mpietrewicz.sp.modules.balance.domain.balance.period.Period;

import java.time.LocalDate;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

import static java.util.function.Predicate.not;

@InternalDomainService
@RequiredArgsConstructor
public class LastMonthCollector extends AllMonthsCollector {

    @Override
    public Period getPeriodCopyFor(Operation operation, List<Operation> operations) {
        LocalDate start = operation.getPeriodStart();
        AtomicReference<Month> checked = new AtomicReference<>(null);
        List<Month> months = getDescendingOperationsUpTo(operations, operation).stream()
                .flatMap(descendingOperation -> getDescendingMonths(descendingOperation).stream())
                .filter(isBefore(checked))
                .peek(checked::set)
                .filter(Month::isValid)
                .filter(not(Month::isUnpaid))
                .limit(2) // todo: sprawdzić czy mogę tu wstawić 1
                .collect(Collectors.toList());

        return new Period(start, months, "period from ChangedPeriodCollector");
    }

}