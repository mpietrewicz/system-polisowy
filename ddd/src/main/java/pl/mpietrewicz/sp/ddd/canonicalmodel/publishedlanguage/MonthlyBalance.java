package pl.mpietrewicz.sp.ddd.canonicalmodel.publishedlanguage;

import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.YearMonth;

@Builder
@Getter
public class MonthlyBalance {

    private final YearMonth month;

    private final String event;

    private final BigDecimal premium;

    private final BigDecimal paid;

}