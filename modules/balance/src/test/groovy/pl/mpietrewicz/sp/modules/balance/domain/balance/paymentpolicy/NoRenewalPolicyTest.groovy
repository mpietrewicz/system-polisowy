package pl.mpietrewicz.sp.modules.balance.domain.balance.paymentpolicy

import pl.mpietrewicz.sp.modules.balance.domain.balance.ComponentPremiumAssembler
import pl.mpietrewicz.sp.modules.balance.domain.balance.month.MonthAssembler
import pl.mpietrewicz.sp.modules.balance.domain.balance.month.MonthStatus
import spock.lang.Specification

import static pl.mpietrewicz.sp.modules.balance.domain.balance.TestUtils.data
import static pl.mpietrewicz.sp.modules.balance.domain.balance.month.MonthStatus.*

class NoRenewalPolicyTest extends Specification {

    def paymentPolicyMock = Mock(PaymentPolicy)

    def renewalPolicy = new NoRenewalPolicy(paymentPolicyMock)

    def monthAssembler = new MonthAssembler()

    def componentPremiumAssembler = new ComponentPremiumAssembler()

    def "should use continuation policy if payment is in period"() {
        given:
        def period = new Period([
                month("2023-01", PAID),
                month("2023-02", UNDERPAID),
                month("2023-03", UNPAID)
        ])

        when:
        renewalPolicy.getMonthToPay(period, paymentData)

        then:
        interaction {
            1 * paymentPolicyMock.getMonthToPay(_,_)
        }

        where:
        paymentData << [
                data("2023-01-10"),
                data("2023-02-20"),
                data("2023-03-31")
        ]
    }

    def "should throw exception when month of payment is out of period"() {
        given:
        def period = new Period([
                month("2023-01", PAID),
                month("2023-02", PAID),
                month("2023-03", UNPAID),
                month("2023-04", UNPAID),
                month("2023-05", UNPAID),
                month("2023-10", PAID),
                month("2023-11", PAID)
        ])

        when:
        renewalPolicy.getMonthToPay(period, paymentData)

        then:
        thrown(Exception)

        where:
        paymentData << [
                data("2022-12-31"),
                data("2023-06-06"),
                data("2023-08-18"),
                data("2023-09-29"),
                data("2023-12-01")
        ]
    }

    def month(String yearMonth, MonthStatus status) {
        def componentPremium = componentPremiumAssembler.builder()
                .withAmount("10")
                .build()

        monthAssembler.builder()
                .withYearMonth(yearMonth)
                .withMonthStatus(status)
                .withComponentPremiums([componentPremium])
                .build()
    }

}