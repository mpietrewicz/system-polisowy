package pl.mpietrewicz.sp.modules.balance.domain.balance.month

import pl.mpietrewicz.sp.ddd.sharedkernel.Amount
import pl.mpietrewicz.sp.modules.balance.domain.balance.month.state.Unpaid

import java.time.YearMonth

class MonthAssembler {

    private YearMonth yearMonth;
    private PaidStatus monthStatus;
    private Amount underpayment = Amount.ZERO;
    private Amount overpayment = Amount.ZERO;

    MonthAssembler builder() {
        this
    }

    MonthAssembler withYearMonth(String yearMonth) {
        this.yearMonth = YearMonth.parse(yearMonth)
        this
    }

    MonthAssembler withMonthStatus(PaidStatus monthStatus) {
        this.monthStatus = monthStatus
        this
    }

    MonthAssembler withUnderpayment(String underpayment) {
        this.underpayment = new Amount(underpayment)
        this
    }

    MonthAssembler withOverpayment(String overpayment) {
        this.overpayment = new Amount(overpayment)
        this
    }

    Month build() {
        new Unpaid(null, null)
    }

}