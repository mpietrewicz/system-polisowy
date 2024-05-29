package pl.mpietrewicz.sp.ddd.canonicalmodel.publishedlanguage;

import lombok.Getter;
import pl.mpietrewicz.sp.ddd.sharedkernel.valueobject.PositiveAmount;

import java.math.BigDecimal;
import java.time.YearMonth;

@Getter
public class MonthlyBalance {

    private final YearMonth month;

    private final PositiveAmount premium;

    private final BigDecimal paid;

    public MonthlyBalance(YearMonth month, PositiveAmount premium, BigDecimal paid) {
        this.month = month;
        this.premium = premium;
        this.paid = paid;
    }

}