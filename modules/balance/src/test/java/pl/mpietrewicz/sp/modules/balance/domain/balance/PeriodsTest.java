package pl.mpietrewicz.sp.modules.balance.domain.balance;

import org.junit.Assert;
import org.junit.Test;
import pl.mpietrewicz.sp.ddd.canonicalmodel.publishedlanguage.AggregateId;
import pl.mpietrewicz.sp.modules.balance.domain.balance.month.ComponentPremium;
import pl.mpietrewicz.sp.modules.balance.domain.balance.month.Month;
import pl.mpietrewicz.sp.modules.balance.domain.balance.month.MonthStatus;

import java.time.YearMonth;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.math.BigDecimal.TEN;
import static java.math.BigDecimal.ZERO;
import static pl.mpietrewicz.sp.modules.balance.domain.balance.month.MonthStatus.UNPAID;

public class PeriodsTest {

    @Test
    public void firstTest() {
        // given
        Month month1 = createMonth("2023-01", null, UNPAID);
        Month month2 = createMonth("2023-02", month1, UNPAID);
        Month month3 = createMonth("2023-03", month2, UNPAID);
        Month month4 = createMonth("2023-04", month3, UNPAID);
        Month month5 = createMonth("2023-05", month4, UNPAID);

        Period period = new Period(Stream.of(month1, month2, month3, month4, month5).collect(Collectors.toList()));

        // when
        AccountingMonth accountingMonth = new AccountingMonth(YearMonth.parse("2023-01"));
        period.includeGracePeriod(accountingMonth);

        // then
        Assert.assertEquals(period.getLastMonth(), month3);
        Assert.assertEquals(period.getMonths().size(), 3);
    }

    private Month createMonth(String yearMonth, Month previousMonth, MonthStatus status) {
        AccountingMonth accountingMonth = new AccountingMonth(YearMonth.parse("2023-01"));
        ComponentPremium componentPremium = new ComponentPremium(AggregateId.generate(), TEN);
        Month nextMonth = new Month(YearMonth.parse(yearMonth), accountingMonth, status, ZERO, ZERO, previousMonth, List.of(componentPremium));

        if (previousMonth != null) {
            previousMonth.setNext(nextMonth);
        }
        return nextMonth;
    }

}