//file:noinspection NonAsciiCharacters
package pl.mpietrewicz.sp.modules.balance.domain.balance.operation

import pl.mpietrewicz.sp.ddd.canonicalmodel.publishedlanguage.AggregateId
import pl.mpietrewicz.sp.modules.balance.domain.balance.Period
import pl.mpietrewicz.sp.modules.balance.domain.balance.month.ComponentPremium
import pl.mpietrewicz.sp.modules.balance.domain.balance.month.Month
import pl.mpietrewicz.sp.modules.balance.domain.balance.month.MonthStatus
import spock.lang.Specification

import java.time.YearMonth

import static java.math.BigDecimal.TEN
import static java.math.BigDecimal.ZERO
import static pl.mpietrewicz.sp.modules.balance.domain.balance.month.MonthStatus.*

class PeriodTest extends Specification {

    def "should reduce period to grace months limit (3 months)"() {
        given:
        def month1 = createMonth("2023-01", null, UNPAID)
        def month2 = createMonth("2023-02", month1, UNPAID)
        def month3 = createMonth("2023-03", month2, UNPAID)
        def month4 = createMonth("2023-04", month3, UNPAID)
        def month5 = createMonth("2023-05", month4, UNPAID)

        def period = new Period([month1, month2, month3, month4, month5] as List)

        when:
        period.includeGracePeriod(3)

        then:
        period.months.size() == 3
    }

    def "should extend period to paid and grace months (6 months)"() {
        given:
        def month1 = createMonth("2023-01", null, PAID)
        def month2 = createMonth("2023-02", month1, PAID)
        def month3 = createMonth("2023-03", month2, PAID)
        def month4 = createMonth("2023-04", month3, UNPAID)

        def period = new Period([month1, month2, month3, month4] as List)

        when:
        period.includeGracePeriod(3)

        then:
        period.months.size() == 6
    }

    def "should extend overpaid period to paid and grace months (9 months)"() {
        given:
        def month1 = createMonth("2023-01", null, PAID)
        def month2 = createMonth("2023-02", month1, PAID)
        def month3 = createMonth("2023-03", month2, OVERPAID, new BigDecimal("30"))

        def period = new Period([month1, month2, month3] as List)

        when:
        period.includeGracePeriod(3)

        then:
        period.months.size() == 9
    }

    def "should extend period to two unpaid months"() {
        given:
        def month1 = createMonth("2023-01", null, PAID)
        def month2 = createMonth("2023-02", month1, UNDERPAID)
        def month3 = createMonth("2023-03", month2, UNPAID)

        def period = new Period([month1, month2, month3] as List)

        when:
        period.includeGracePeriod(3)

        then:
        period.months.size() == 4
    }

    def createMonth(String yearMonth, Month previousMonth, MonthStatus status) {
        return createMonth(yearMonth, previousMonth, status, ZERO)
    }

    def createMonth(String yearMonth, Month previousMonth, MonthStatus status, BigDecimal overpayment) {
        def componentPremium = new ComponentPremium(AggregateId.generate(), TEN)
        def nextMonth = new Month(YearMonth.parse(yearMonth), status, ZERO, overpayment, previousMonth, [componentPremium])

        if (previousMonth != null) {
            previousMonth.next = nextMonth
        }
        return nextMonth
    }

}