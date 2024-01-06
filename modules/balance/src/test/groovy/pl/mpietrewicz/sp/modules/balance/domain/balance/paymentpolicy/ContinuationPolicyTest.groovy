package pl.mpietrewicz.sp.modules.balance.domain.balance.paymentpolicy

import pl.mpietrewicz.sp.ddd.canonicalmodel.publishedlanguage.snapshot.premium.PremiumSnapshot

import pl.mpietrewicz.sp.modules.balance.domain.balance.month.MonthAssembler
import pl.mpietrewicz.sp.modules.balance.domain.balance.month.MonthStatus
import spock.lang.Specification

import static pl.mpietrewicz.sp.modules.balance.domain.balance.TestUtils.data
import static pl.mpietrewicz.sp.modules.balance.domain.balance.TestUtils.yearMonth
import static pl.mpietrewicz.sp.modules.balance.domain.balance.month.MonthStatus.*

class ContinuationPolicyTest extends Specification {

    def premiumSnapshot = PremiumSnapshot.builder().build()

    def continuationPolicy = new ContinuationPolicy(premiumSnapshot)

    def monthAssembler = new MonthAssembler()

    def "should return first month of period if period is unpaid"() {
        given:
        def period = new Period([
                month("2023-01", UNPAID),
                month("2023-02", UNPAID),
                month("2023-03", UNPAID),
        ])

        expect:
        continuationPolicy.getMonthToPay(period, paymentData).getYearMonth() == monthToPay

        where:
        paymentData        || monthToPay
        data("2022-10-10") || yearMonth("2023-01")
        data("2023-01-01") || yearMonth("2023-01")
        data("2023-02-12") || yearMonth("2023-01")
        data("2023-03-31") || yearMonth("2023-01")
        data("2023-04-01") || yearMonth("2023-01")
    }

    def "should return last paid month if period is paid"() {
        given:
        def period = new Period([
                month("2023-01", PAID),
                month("2023-02", PAID),
                month("2023-03", PAID)
        ])

        expect:
        continuationPolicy.getMonthToPay(period, paymentData).getYearMonth() == monthToPay

        where:
        paymentData        || monthToPay
        data("2022-10-10") || yearMonth("2023-03")
        data("2023-01-01") || yearMonth("2023-03")
        data("2023-03-31") || yearMonth("2023-03")
        data("2023-04-01") || yearMonth("2023-03")
    }

    def "should return last month if period is overpaid"() {
        given:
        def period = new Period([
                month("2023-01", PAID),
                month("2023-02", PAID),
                month("2023-03", OVERPAID)
        ])

        expect:
        continuationPolicy.getMonthToPay(period, paymentData).getYearMonth() == monthToPay

        where:
        paymentData        || monthToPay
        data("2022-10-10") || yearMonth("2023-03")
        data("2023-01-01") || yearMonth("2023-03")
        data("2023-03-31") || yearMonth("2023-03")
        data("2023-04-01") || yearMonth("2023-03")
    }

    def "should return first unpaid month if period is partly paid"() {
        given:
        def period = new Period([
                month("2023-01", PAID),
                month("2023-02", UNDERPAID),
                month("2023-03", UNPAID)
        ])

        expect:
        continuationPolicy.getMonthToPay(period, paymentData).getYearMonth() == monthToPay

        where:
        paymentData        || monthToPay
        data("2022-10-10") || yearMonth("2023-02")
        data("2023-01-01") || yearMonth("2023-02")
        data("2023-03-31") || yearMonth("2023-02")
        data("2023-04-01") || yearMonth("2023-02")
    }

    def month(String yearMonth, MonthStatus status) {

        monthAssembler.builder()
                .withYearMonth(yearMonth)
                .withMonthStatus(status)
                .build()
    }

}