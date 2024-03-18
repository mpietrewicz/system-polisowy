package pl.mpietrewicz.sp.ddd.canonicalmodel.publishedlanguage.snapshot.premium;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import pl.mpietrewicz.sp.ddd.annotations.domain.ValueObject;
import pl.mpietrewicz.sp.ddd.canonicalmodel.publishedlanguage.AggregateId;
import pl.mpietrewicz.sp.ddd.sharedkernel.Amount;

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
        if (date.isBefore(start)) {
            return Amount.ZERO; // todo: tutaj też mogę zwracać Illegalstate, gdy nie znajdę składki
        } else if (end != null && date.isAfter(end)) {
            return Amount.ZERO; // todo: tutaj też mogę zwracać Illegalstate, gdy nie znajdę składki
        } else {
            return changes.stream()
                    .filter(change -> change.isBefore(date))
                    .max(ChangePremiumSnapshot::orderComparator)
                    .map(ChangePremiumSnapshot::getAmount)
                    .orElse(initialAmount);
        }
    }

    public Amount getPremiumAt(YearMonth month) {
        return getPremiumAt(month.atDay(1));
    }

}