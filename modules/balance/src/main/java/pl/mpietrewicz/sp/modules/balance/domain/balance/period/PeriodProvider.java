package pl.mpietrewicz.sp.modules.balance.domain.balance.period;

import pl.mpietrewicz.sp.ddd.canonicalmodel.publishedlanguage.MonthlyBalance;

import java.time.YearMonth;
import java.util.List;

public interface PeriodProvider {

    List<MonthlyBalance> getMonthlyBalances();

    List<YearMonth> getRenewalMonths();

}