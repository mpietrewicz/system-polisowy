package pl.mpietrewicz.sp.ddd.canonicalmodel.publishedlanguage;

import lombok.Getter;
import pl.mpietrewicz.sp.ddd.sharedkernel.valueobject.Amount;

import java.time.YearMonth;

@Getter
public class MonthlyBalance {

    private final YearMonth month;

    private final Amount premium;

    private final Amount paid;

    public MonthlyBalance(YearMonth month, Amount premium, Amount paid) {
        this.month = month;
        this.premium = premium;
        this.paid = paid;
    }

}