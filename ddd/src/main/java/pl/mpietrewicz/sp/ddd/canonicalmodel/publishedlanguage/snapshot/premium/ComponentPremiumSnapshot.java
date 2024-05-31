package pl.mpietrewicz.sp.ddd.canonicalmodel.publishedlanguage.snapshot.premium;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import pl.mpietrewicz.sp.ddd.annotations.domain.ValueObject;
import pl.mpietrewicz.sp.ddd.canonicalmodel.publishedlanguage.AggregateId;
import pl.mpietrewicz.sp.ddd.sharedkernel.valueobject.PositiveAmount;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import java.util.Optional;

@ValueObject
@Getter
@Builder
@EqualsAndHashCode(of = {"componentId", "start", "initialAmount", "changes", "end"})
public class ComponentPremiumSnapshot {

    private AggregateId componentId;
    private LocalDate start;
    private PositiveAmount initialPremium;
    private List<ChangePremiumSnapshot> changes;
    private LocalDate end;

    public Optional<PositiveAmount> getPremiumAt(LocalDate date) {
        if (isBetweenStartAdnEnd(date)) {
            return Optional.of(changes.stream()
                    .filter(change -> change.isBeforeOrEquals(date))
                    .max(ChangePremiumSnapshot::orderComparator)
                    .map(ChangePremiumSnapshot::getPremium)
                    .orElse(initialPremium));
        } else {
            return Optional.empty();
        }
    }

    private boolean isBetweenStartAdnEnd(LocalDate date) {
        return date.compareTo(start) >= 0
                && (end == null || date.compareTo(end) <= 0);
    }

    public PositiveAmount getCurrentPremium() {
        return getPremiumAt(LocalDate.now())
                .orElseThrow();
    }

    public LocalDate getValidFrom() {
        LocalDate now = LocalDate.now();
        return changes.stream()
                .filter(change -> change.isBeforeOrEquals(now))
                .max(ChangePremiumSnapshot::orderComparator)
                .map(ChangePremiumSnapshot::getDate)
                .orElse(start);
    }

}