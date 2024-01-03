package pl.mpietrewicz.sp.modules.balance.domain.balance.operation

import pl.mpietrewicz.sp.ddd.canonicalmodel.publishedlanguage.PaymentPolicyEnum
import pl.mpietrewicz.sp.modules.balance.domain.balance.operation.type.AddPayment
import pl.mpietrewicz.sp.modules.balance.domain.balance.operation.type.AddRefund
import pl.mpietrewicz.sp.modules.balance.domain.balance.operation.type.ChangePremium
import pl.mpietrewicz.sp.modules.balance.domain.balance.operation.type.StartCalculating
import spock.lang.Specification

import java.time.LocalDate
import java.time.YearMonth

class OperationComparingTest extends Specification {

    def "startCalculating should always be first (before any operation)"() {
        given:
        def addPayment = prepareAddPayment("2023-02-28")
        def startCalculating = prepareStartCalculating("2023-03-01")

        expect:
        startCalculating.isBefore(addPayment)
        addPayment.isAfter(startCalculating)
    }

    def "non startCalculating operations should comparing by default conditions - by date"() {
        given:
        def addPayment1 = prepareAddPayment("2023-02-28")
        def addPayment2 = prepareAddPayment("2023-03-31")

        expect:
        addPayment1.isBefore(addPayment2)
        addPayment2.isAfter(addPayment1)
    }

    def "should order other operations starting on startCalculating"() {
        given:
        def addPayment1 = prepareAddPayment("2023-01-10")
        sleep(10)
        def addPayment2 = prepareAddPayment("2023-02-21")
        sleep(10)
        def startCalculating = prepareStartCalculating("2023-03-15")
        sleep(10)
        def addPayment3 = prepareAddPayment("2023-02-20")
        sleep(10)
        def addRefund1 = prepareAddRefund("2023-02-20")
        sleep(10)
        def addRefund2 = prepareAddRefund("2023-02-20")
        sleep(10)
        def changePremium1 = prepareChangePremium("2023-04-01")
        sleep(10)
        def changePremium2 = prepareChangePremium("2023-03-15")

        def sortedOperations = [startCalculating, addPayment1, addPayment3, addRefund1, addRefund2,
                          addPayment2, changePremium2, changePremium1]

        expect:
        (0..<sortedOperations.size() - 1).every { index ->
            sortedOperations[index].orderComparator(sortedOperations[index + 1]) < 0
        }
    }

    private static StartCalculating prepareStartCalculating(String date) {
        new StartCalculating(YearMonth.from(LocalDate.parse(date)), null, null)
    }

    private static AddPayment prepareAddPayment(String date) {
        new AddPayment(LocalDate.parse(date), null, PaymentPolicyEnum.CONTINUATION)
    }

    private static AddRefund prepareAddRefund(String date) {
        new AddRefund(LocalDate.parse(date), null)
    }

    private static ChangePremium prepareChangePremium(String date) {
        new ChangePremium(LocalDate.parse(date), null)
    }

}