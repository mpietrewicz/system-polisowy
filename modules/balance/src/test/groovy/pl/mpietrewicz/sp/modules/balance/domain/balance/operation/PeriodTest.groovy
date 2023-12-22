//file:noinspection NonAsciiCharacters
package pl.mpietrewicz.sp.modules.balance.domain.balance.operation

import pl.mpietrewicz.sp.ddd.sharedkernel.Amount
import pl.mpietrewicz.sp.modules.balance.domain.balance.ComponentPremiumAssembler
import pl.mpietrewicz.sp.modules.balance.domain.balance.Premium
import pl.mpietrewicz.sp.modules.balance.domain.balance.PremiumAssembler
import pl.mpietrewicz.sp.modules.balance.domain.balance.month.MonthAssembler
import pl.mpietrewicz.sp.modules.balance.domain.balance.month.MonthStatus
import pl.mpietrewicz.sp.modules.balance.domain.balance.paymentpolicy.Period
import spock.lang.Specification

import java.time.YearMonth

import static pl.mpietrewicz.sp.modules.balance.domain.balance.month.MonthStatus.*

class PeriodTest extends Specification {

    def monthAssembler = new MonthAssembler()

    def componentPremiumAssembler = new ComponentPremiumAssembler()

    def premiumAssembler = new PremiumAssembler()

    def "should reduce period to grace months limit (3 months)"() {
        given:
        def period = new Period([
                month("2023-01", UNPAID),
                month("2023-02", UNPAID),
                month("2023-03", UNPAID),
                month("2023-04", UNPAID),
                month("2023-05", UNPAID)
        ])

        when:
        period.includeGracePeriod(_ as Premium, 3)

        then:
        period.getLastMonthOfLiability() == YearMonth.parse("2023-03")
    }

    def "should extend period to paid and grace months (6 months)"() {
        given:
        def period = new Period([
                month("2023-01", PAID),
                month("2023-02", PAID),
                month("2023-03", PAID),
                month("2023-04", UNPAID)
        ])

        when:
        period.includeGracePeriod(_ as Premium, 3)

        then:
        period.getLastMonthOfLiability() == YearMonth.parse("2023-06")
    }

    def "should extend overpaid period to paid and grace months (9 months)"() {
        given:
        def period = new Period([
                month("2023-01", PAID),
                month("2023-02", PAID),
                month("2023-03", OVERPAID, "30")
        ])
        def componentPremium = componentPremiumAssembler.builder()
                .withAmount("15")
                .build()
        def premium = premiumAssembler.builder()
                .addComponentPremium(componentPremium)
                .build()

        when:
        period.includeGracePeriod(premium, 3)

        then:
        period.getLastMonthOfLiability() == YearMonth.parse("2023-08")
    }

    def "should extend period to two unpaid months"() {
        given:
        def period = new Period([
                month("2023-01", PAID),
                month("2023-02", UNDERPAID),
                month("2023-03", UNPAID)
        ])

        when:
        period.includeGracePeriod(_ as Premium, 3)

        then:
        period.getLastMonthOfLiability() == YearMonth.parse("2023-04")
    }

    def month(String yearMonth, MonthStatus status) {
        month(yearMonth, status, Amount.ZERO as String)
    }

    def month(String yearMonth, MonthStatus status, String overpayment) {
        def componentPremium = componentPremiumAssembler.builder()
                .withAmount("10")
                .build()
        monthAssembler.builder()
                .withYearMonth(yearMonth)
                .withMonthStatus(status)
                .withOverpayment(overpayment)
                .withComponentPremiums([componentPremium])
                .build()
    }

}