package pl.mpietrewicz.sp.ddd.canonicalmodel.publishedlanguage.snapshot.premium;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import pl.mpietrewicz.sp.ddd.annotations.domain.ValueObject;
import pl.mpietrewicz.sp.ddd.canonicalmodel.publishedlanguage.AggregateId;
import pl.mpietrewicz.sp.ddd.sharedkernel.valueobject.Amount;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;

@ValueObject
@Getter
@Builder
@EqualsAndHashCode(of = {"componentId", "start", "initialAmount", "changes", "end"})
public class ComponentPremiumSnapshot {

    private AggregateId componentId;
    private LocalDate start;
    private Amount initialAmount;
    private List<ChangePremiumSnapshot> changes;
    private LocalDate end;

    public Amount getPremiumAt(LocalDate date) {
        if (isBetweenStartAdnEnd(date)) {
            return changes.stream()
                    .filter(change -> change.isBeforeOrEquals(date))
                    .max(ChangePremiumSnapshot::orderComparator)
                    .map(ChangePremiumSnapshot::getAmount)
                    .orElse(initialAmount);
        } else {
            return Amount.ZERO;
        }
    }

    public Amount getPremiumAt(YearMonth month) {
        return getPremiumAt(month.atDay(1));
    }

    private boolean isBetweenStartAdnEnd(LocalDate date) {
        return date.compareTo(start) >= 0
                && (end == null || date.compareTo(end) <= 0);
    }

    public Amount getCurrentPremium() { // todo: połączyć to ze zwracaną warością
        return getPremiumAt(LocalDate.now());
    }

    public LocalDate getValidFrom() { // todo: połączyć to ze zwracaną warością
        LocalDate now = LocalDate.now();
        return changes.stream()
                .filter(change -> change.isBeforeOrEquals(now))
                .max(ChangePremiumSnapshot::orderComparator)
                .map(ChangePremiumSnapshot::getDate)
                .orElse(start);
    }

}