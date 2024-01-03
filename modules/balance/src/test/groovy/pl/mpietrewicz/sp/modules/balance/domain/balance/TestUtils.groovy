package pl.mpietrewicz.sp.modules.balance.domain.balance

import pl.mpietrewicz.sp.ddd.sharedkernel.Amount
import pl.mpietrewicz.sp.modules.balance.domain.balance.operation.PaymentData

import java.time.LocalDate
import java.time.YearMonth

class TestUtils {

    static LocalDate date(String str) {
        LocalDate.parse(str)
    }

    static YearMonth yearMonth(String str) {
        YearMonth.parse(str)
    }

    static PaymentData data(String date) {
        new PaymentData(LocalDate.parse(date), Amount.TEN)
    }

    static PaymentData data(String date, String amount) {
        new PaymentData(LocalDate.parse(date), new Amount(amount))
    }

}