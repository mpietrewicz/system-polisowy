package pl.mpietrewicz.sp.ddd.canonicalmodel.publishedlanguage.snapshot.premium;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import pl.mpietrewicz.sp.ddd.annotations.domain.ValueObject;
import pl.mpietrewicz.sp.ddd.sharedkernel.valueobject.Amount;

import java.time.LocalDate;

@ValueObject
@Getter
@Builder
@EqualsAndHashCode(of = {"date", "amount"})
public class ChangePremiumSnapshot {

    LocalDate date;
    Amount amount;

    protected boolean isBeforeOrEquals(LocalDate date) {
        return this.date.compareTo(date) <= 0;
    }

    public int orderComparator(ChangePremiumSnapshot changePremiumSnapshot) {
        return this.date.compareTo(changePremiumSnapshot.date);
    }

}