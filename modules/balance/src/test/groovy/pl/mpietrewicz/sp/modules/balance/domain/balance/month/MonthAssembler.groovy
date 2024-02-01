package pl.mpietrewicz.sp.modules.balance.domain.balance.month

import pl.mpietrewicz.sp.ddd.sharedkernel.Amount

import java.time.YearMonth

class MonthAssembler {

    private YearMonth yearMonth;
    private MonthStatus monthStatus;
    private Amount underpayment = Amount.ZERO;
    private Amount overpayment = Amount.ZERO;

    MonthAssembler builder() {
        this
    }

    MonthAssembler withYearMonth(String yearMonth) {
        this.yearMonth = YearMonth.parse(yearMonth)
        this
    }

    MonthAssembler withMonthStatus(MonthStatus monthStatus) {
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
        new Month(yearMonth, monthStatus, Amount.ZERO, Amount.ZERO)
    }

}