package pl.mpietrewicz.sp.modules.balance.domain.balance.period.collector;

import io.micrometer.core.annotation.Timed;
import pl.mpietrewicz.sp.ddd.annotations.domain.InternalDomainService;
import pl.mpietrewicz.sp.modules.balance.domain.balance.month.ChangeStatus;
import pl.mpietrewicz.sp.modules.balance.domain.balance.month.Month;
import pl.mpietrewicz.sp.modules.balance.domain.balance.period.PartialPeriod;
import pl.mpietrewicz.sp.modules.balance.domain.balance.period.Period;

import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static java.util.function.Predicate.not;

@InternalDomainService
public class PartialPeriodCollector {

    @Timed(value = "PartialPeriodCollector.getPartialPeriod")
    public PartialPeriod getPartialPeriod(Period previousPeriod, Period currentPeriod) {
        List<Month> previousMonths = previousPeriod.getMonths();
        List<Month> currentMonths = currentPeriod.getMonths();

        removeTheSameMonths(previousMonths, currentMonths);

        currentMonths.stream()
                .filter(not(hasTheSameYearMonths(previousMonths)))
                .forEach(current -> current.setAs(ChangeStatus.ADDED));

        currentMonths.stream()
                .filter(hasTheSameYearMonths(previousMonths))
                .forEach(current -> current.setAs(ChangeStatus.CHANGED));

        List<Month> removed = previousMonths.stream()
                .filter(not(hasTheSameYearMonths(currentMonths)))
                .collect(Collectors.toList());
        removed.forEach(month -> month.setAs(ChangeStatus.REMOVED));
        currentMonths.addAll(removed);

        return new PartialPeriod(currentPeriod.getStart(), currentMonths, true, currentPeriod.getInfo());
    }

    private void removeTheSameMonths(List<Month> previousMonths, List<Month> currentMonths) {
        Iterator<Month> currentIterator = currentMonths.stream()
                .sorted(Month::compareAscending).iterator();
        while (currentIterator.hasNext()) {
            Month current = currentIterator.next();
            Optional<Month> theSamePrevious = previousMonths.stream()
                    .filter(previous -> previous.isTheSame(current))
                    .findAny();
            if (theSamePrevious.isPresent()) {
                previousMonths.remove(theSamePrevious.get());
                currentMonths.remove(current);
            } else {
                break;
            }
        }
    }

    private Predicate<Month> hasTheSameYearMonths(List<Month> months) {
        return month -> months.stream()
                .anyMatch(month::isTheSameYearMonth);
    }

}