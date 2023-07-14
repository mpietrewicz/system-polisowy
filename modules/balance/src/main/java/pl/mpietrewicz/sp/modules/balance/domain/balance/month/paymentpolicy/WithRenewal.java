package pl.mpietrewicz.sp.modules.balance.domain.balance.month.paymentpolicy;

import pl.mpietrewicz.sp.modules.balance.domain.balance.Period;
import pl.mpietrewicz.sp.modules.balance.domain.balance.month.PaymentPolicy;
import pl.mpietrewicz.sp.modules.balance.domain.balance.month.Month;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.Optional;

public class WithRenewal implements PaymentPolicy {

    @Override
    public Month getFirstMonthToPay(Period period, LocalDate paymentDate) {
        Month monthOfPayment = getMonthOfPayment(period, paymentDate);
        if (monthOfPayment.isNotPaid()) {
            return getFirstPaidMonthBefore(paymentDate, period)
                    .orElse(getFirstMonth(period));
        } else {
            return monthOfPayment;
        }
    }

    private Optional<Month> getFirstPaidMonthBefore(LocalDate paymentDate, Period period) {
        return period.getMonths().stream()
                .filter(month -> month.getYearMonth().isBefore(YearMonth.from(paymentDate)))
                .filter(Month::isPaid)
                .max(Month::orderComparator);
    }

    private Month getMonthOfPayment(Period period, LocalDate paymentDate) {
        return period.getMonths().stream()
                .filter(month -> month.getYearMonth().equals(YearMonth.from(paymentDate)))
                .findAny()
                .orElseThrow(() -> new IllegalStateException("Musi istnieć miesiąc za który chcemy dodać płatność - to inaczej: nie można dodać wpłaty w prszyszłość"));
    }

    private Month getFirstMonth(Period period) {
        return period.getMonths().stream()
                .min(Month::orderComparator)
                .orElseThrow();
    }

}