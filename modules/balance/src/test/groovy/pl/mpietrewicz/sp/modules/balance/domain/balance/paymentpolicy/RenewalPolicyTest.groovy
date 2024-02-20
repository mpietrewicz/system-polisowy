package pl.mpietrewicz.sp.modules.balance.domain.balance.paymentpolicy

import pl.mpietrewicz.sp.ddd.canonicalmodel.publishedlanguage.snapshot.premium.PremiumSnapshot
import pl.mpietrewicz.sp.modules.balance.domain.balance.Period
import pl.mpietrewicz.sp.modules.balance.domain.balance.month.MonthAssembler
import pl.mpietrewicz.sp.modules.balance.domain.balance.month.PaidStatus
import pl.mpietrewicz.sp.modules.balance.domain.balance.paymentpolicy.renewal.WithoutUnderpayment
import spock.lang.Specification

import static pl.mpietrewicz.sp.modules.balance.domain.balance.TestUtils.data
import static pl.mpietrewicz.sp.modules.balance.domain.balance.TestUtils.yearMonth
import static pl.mpietrewicz.sp.modules.balance.domain.balance.month.PaidStatus.*

class RenewalPolicyTest extends Specification {

    def paymentPolicyMock = Mock(PaymentPolicy)

    RenewalPolicy renewalPolicy

    def monthAssembler = new MonthAssembler()

    def setup() {
        def premiumSnapshot = PremiumSnapshot.builder().build()

        renewalPolicy = new WithoutUnderpayment(paymentPolicyMock, premiumSnapshot)
    }

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

    def "should return created renewal month with month of payment if payment can paid month"() {
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

        expect:
        renewalPolicy.getMonthToPay(period, paymentData).getYearMonth() == monthToPay

        where:
        paymentData         || monthToPay
        data("2022-12-31")  || yearMonth("2022-12")
        data("2023-06-06")  || yearMonth("2023-06")
        data("2023-08-18")  || yearMonth("2023-08")
        data("2023-09-29")  || yearMonth("2023-09")
        data("2023-12-01")  || yearMonth("2023-12")
    }

        def "should throw exception if created renewal month can not paid"() {
        given:
        def period = new Period([
                month("2023-01", PAID),
                month("2023-02", UNPAID),
                month("2023-03", UNPAID),
                month("2023-04", UNPAID),
        ])

        when:
        renewalPolicy.getMonthToPay(period, paymentData)

        then:
        thrown(Exception)

        where:
        paymentData << [
                data("2022-05-01", "5"),
                data("2023-06-06", "9"),
        ]
    }

    def month(String yearMonth, PaidStatus status) {
        monthAssembler.builder()
                .withYearMonth(yearMonth)
                .withMonthStatus(status)
                .build()
    }

}