package pl.mpietrewicz.sp.ddd.canonicalmodel.publishedlanguage;

import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.YearMonth;
import java.util.Map;

@Builder
@Getter
public class MonthlyBalance {

    private final YearMonth month;

    private final Map<AggregateId, BigDecimal> componentPremiums;

    private final boolean isPaid;

}