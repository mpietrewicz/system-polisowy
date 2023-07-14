package pl.mpietrewicz.sp.modules.balance.domain.balance.month.paymentpolicy;

import pl.mpietrewicz.sp.modules.balance.domain.balance.month.PaymentPolicy;
import pl.mpietrewicz.sp.modules.balance.domain.balance.month.Month;
import pl.mpietrewicz.sp.modules.balance.domain.balance.Period;

import java.time.LocalDate;
import java.util.Comparator;

public class WithoutLimits implements PaymentPolicy {

    @Override
    public Month getFirstMonthToPay(Period period, LocalDate paymentDate) {
        return period.getMonths().stream()
                .filter(Month::isNotPaid)
                .min(Comparator.comparing(Month::getYearMonth))
                .orElse(period.getFirstMonth());
    }

}