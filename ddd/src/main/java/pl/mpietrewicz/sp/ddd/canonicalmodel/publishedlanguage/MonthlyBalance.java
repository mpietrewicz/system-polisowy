package pl.mpietrewicz.sp.ddd.canonicalmodel.publishedlanguage;

import lombok.Builder;
import lombok.Getter;
import pl.mpietrewicz.sp.ddd.sharedkernel.Amount;

import java.time.YearMonth;
import java.util.Map;

@Builder
@Getter
public class MonthlyBalance {

    private final YearMonth month;

    private final Map<AggregateId, Amount> componentPremiums;

    private final boolean isPaid;

}