package pl.mpietrewicz.sp.modules.balance.domain.balance.month.paymentpolicy;

import pl.mpietrewicz.sp.modules.balance.domain.balance.Period;
import pl.mpietrewicz.sp.modules.balance.domain.balance.month.Month;

import java.time.LocalDate;
import java.time.YearMonth;
import java.time.temporal.ChronoUnit;

public class WithoutRenewal extends WithRenewal {

    @Override
    public Month getFirstMonthToPay(Period period, LocalDate paymentDate) {
        Month firstMonthToPay = super.getFirstMonthToPay(period, paymentDate);

        if (getMonthsBetween(paymentDate, firstMonthToPay) <= 3) {
            return firstMonthToPay;
        } else {
            throw new IllegalStateException("Nie można wznowić umowy - minął okres powyzej 3 miesiący");
        }
    }

    private int getMonthsBetween(LocalDate paymentDate, Month firstMonthToPay) {
        return getMonthsBetween(firstMonthToPay.getYearMonth(), YearMonth.from(paymentDate));
    }

    private int getMonthsBetween(YearMonth from, YearMonth to) {
        return Long.valueOf(ChronoUnit.MONTHS.between(from, to)).intValue();
    }

}