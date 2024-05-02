package pl.mpietrewicz.sp.modules.balance.domain.balance.period.collector;

import lombok.RequiredArgsConstructor;
import pl.mpietrewicz.sp.ddd.annotations.domain.InternalDomainService;
import pl.mpietrewicz.sp.modules.balance.domain.balance.month.ChangeStatus;
import pl.mpietrewicz.sp.modules.balance.domain.balance.month.Month;
import pl.mpietrewicz.sp.modules.balance.domain.balance.operation.Operation;
import pl.mpietrewicz.sp.modules.balance.domain.balance.period.PartialPeriod;
import pl.mpietrewicz.sp.modules.balance.domain.balance.period.Period;

import java.time.LocalDate;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static java.util.function.Predicate.not;

@InternalDomainService
@RequiredArgsConstructor
public class AllMonthsCollector implements PeriodCollector {

    @Override
    public Period getPeriodCopyFor(Operation operation, List<Operation> operations) {
        LocalDate start = operation.getPeriodStart();
        AtomicReference<Month> checked = new AtomicReference<>(null);
        List<Month> months = getDescendingOperationsUpTo(operations, operation).stream()
                .flatMap(descendingOperation -> getDescendingMonths(descendingOperation).stream())
                .filter(isBefore(checked))
                .peek(checked::set)
                .filter(Month::isValid)
                .map(Month::createCopy)
                .collect(Collectors.toList());

        return new Period(start, months, "period from ChangedPeriodCollector");
    }

    @Override
    public PartialPeriod getPartialPeriodToSave(Period previousPeriod, Period currentPeriod) {
        Iterator<Month> currentIterator = currentPeriod.getMonths().stream()
                .sorted(Month::compareAscending).iterator();
        while (currentIterator.hasNext()) {
            Month current = currentIterator.next();
            Optional<Month> theSamePrevious = previousPeriod.getMonths().stream()
                    .filter(previous -> previous.isTheSame(current))
                    .findAny();
            if (theSamePrevious.isPresent()) {
                previousPeriod.getMonths().remove(theSamePrevious.get());
                currentPeriod.getMonths().remove(current);
            } else {
                break;
            }
        }

        currentPeriod.getMonths().stream()
                .filter(not(hasTheSameYearMonths(previousPeriod)))
                .forEach(current -> current.setAs(ChangeStatus.ADDED));

        currentPeriod.getMonths().stream()
                .filter(hasTheSameYearMonths(previousPeriod))
                .forEach(current -> current.setAs(ChangeStatus.CHANGED));

        List<Month> removed = previousPeriod.getMonths().stream()
                .filter(not(hasTheSameYearMonths(currentPeriod)))
                .collect(Collectors.toList());
        removed.forEach(month -> month.setAs(ChangeStatus.REMOVED));
        currentPeriod.getMonths().addAll(removed);

        return new PartialPeriod(currentPeriod.getStart(), currentPeriod.getMonths(), true, currentPeriod.getInfo());
    }

    protected List<Month> getDescendingMonths(Operation operation) {
        return operation.getPeriodMonths().stream()
                .sorted(Month::compareDescending)
                .collect(Collectors.toList());
    }

    protected Predicate<Month> isBefore(AtomicReference<Month> checked) {
        return month -> checked.get() == null || month.isBefore(checked.get());
    }

    private Predicate<Month> hasTheSameYearMonths(Period period) {
        return month -> period.getMonths().stream()
                .anyMatch(month::isTheSameYearMonth);
    }

    public List<Operation> getDescendingOperationsUpTo(List<Operation> operations, Operation operation) {
        return operations.stream()
                .filter(not(o -> o.isAfter(operation)))
                .sorted(Operation::reverseOrderComparator)
                .collect(Collectors.toList());
    }

}