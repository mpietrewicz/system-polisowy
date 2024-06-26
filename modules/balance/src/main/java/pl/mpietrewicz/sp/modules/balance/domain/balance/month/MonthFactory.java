package pl.mpietrewicz.sp.modules.balance.domain.balance.month;

import pl.mpietrewicz.sp.ddd.canonicalmodel.publishedlanguage.snapshot.premium.PremiumSnapshot;
import pl.mpietrewicz.sp.ddd.sharedkernel.valueobject.PositiveAmount;

import java.time.YearMonth;

public class MonthFactory {

    public static LastMonth create(YearMonth yearMonth, PremiumSnapshot premiumSnapshot, boolean renewal) {
        PositiveAmount premium = premiumSnapshot.getPremiumAt(yearMonth);
        return new Month(yearMonth, premium, renewal);
    }

}