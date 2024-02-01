package pl.mpietrewicz.sp.modules.balance.domain.balance.paymentpolicy;

import lombok.RequiredArgsConstructor;
import pl.mpietrewicz.sp.ddd.annotations.domain.DomainPolicyImpl;
import pl.mpietrewicz.sp.ddd.canonicalmodel.publishedlanguage.snapshot.premium.PremiumSnapshot;
import pl.mpietrewicz.sp.ddd.sharedkernel.PositiveAmount;
import pl.mpietrewicz.sp.modules.balance.domain.balance.MonthToPay;
import pl.mpietrewicz.sp.modules.balance.domain.balance.Period;
import pl.mpietrewicz.sp.modules.balance.domain.balance.month.Month;
import pl.mpietrewicz.sp.modules.balance.domain.balance.operation.PaymentData;

import java.time.YearMonth;

@DomainPolicyImpl
@RequiredArgsConstructor
public class ContinuationPolicy implements PaymentPolicy {

    private final PremiumSnapshot premiumSnapshot;

    @Override
    public MonthToPay getMonthToPay(Period period, PaymentData paymentData) {
        YearMonth lastPaidMonth = period.getLastPaidYearMonth();
        YearMonth nextYearMonth = lastPaidMonth.plusMonths(1);
        Month nextMonth = period.getMonthOf(nextYearMonth) // todo: bo może być unpaid -> troche to nieczytalne, czy można to jakoś udoskonalić ?
                .orElseGet(() -> {
                    PositiveAmount premium = premiumSnapshot.getAmountAt(nextYearMonth.atDay(1));
                    Month month = Period.createMonth(nextYearMonth, premium);
                    period.addNewMonth(month);
                    return month;
                });
        return new MonthToPay(nextMonth, paymentData.getAmount());
    }

}