package pl.mpietrewicz.sp.modules.balance.domain.balance;

import pl.mpietrewicz.sp.ddd.canonicalmodel.publishedlanguage.MonthlyBalance;
import pl.mpietrewicz.sp.ddd.canonicalmodel.publishedlanguage.snapshot.premium.PremiumSnapshot;

import java.time.YearMonth;
import java.util.List;

public interface PeriodProvider {

    List<MonthlyBalance> getMonthlyBalances(PremiumSnapshot premiumSnapshot);

    List<YearMonth> getRenewalMonths();

}