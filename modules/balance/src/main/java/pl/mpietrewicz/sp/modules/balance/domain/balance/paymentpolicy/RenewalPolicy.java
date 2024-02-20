package pl.mpietrewicz.sp.modules.balance.domain.balance.paymentpolicy;

import lombok.RequiredArgsConstructor;
import pl.mpietrewicz.sp.ddd.annotations.domain.DomainPolicyImpl;
import pl.mpietrewicz.sp.ddd.canonicalmodel.publishedlanguage.snapshot.premium.PremiumSnapshot;
import pl.mpietrewicz.sp.ddd.sharedkernel.Amount;
import pl.mpietrewicz.sp.ddd.sharedkernel.PositiveAmount;
import pl.mpietrewicz.sp.modules.balance.domain.balance.MonthToPay;
import pl.mpietrewicz.sp.modules.balance.domain.balance.Period;
import pl.mpietrewicz.sp.modules.balance.domain.balance.month.Month;
import pl.mpietrewicz.sp.modules.balance.domain.balance.operation.PaymentData;
import pl.mpietrewicz.sp.modules.balance.domain.balance.paymentpolicy.PaymentPolicy;
import pl.mpietrewicz.sp.modules.balance.exceptions.RenewalException;

import java.time.YearMonth;

import static pl.mpietrewicz.sp.DateUtils.getMonthsBetween;

@DomainPolicyImpl
@RequiredArgsConstructor
public abstract class RenewalPolicy implements PaymentPolicy {

    private final PaymentPolicy continuationPolicy;
    private final PremiumSnapshot premiumSnapshot;
    private final int graceMonths;

    @Override
    public MonthToPay getMonthToPay(Period period, PaymentData paymentData) throws RenewalException {
        YearMonth lastPaidMonth = period.getLastPaidYearMonth();
        YearMonth paymentMonth = YearMonth.from(paymentData.getDate());

        if (getMonthsBetween(lastPaidMonth, paymentMonth) <= graceMonths) {
            return continuationPolicy.getMonthToPay(period, paymentData);
        } else {
            return tryCreateRenewalMonth(period, paymentData);
        }
    }

    private MonthToPay tryCreateRenewalMonth(Period period, PaymentData paymentData) throws RenewalException {
        YearMonth paymentMonth = YearMonth.from(paymentData.getDate());
        PositiveAmount premium = premiumSnapshot.getAmountAt(paymentData.getDate());
        Amount amountToRenew = getAmountToRenew(period, paymentData);
        Month renewalMonth = Period.createMonth(paymentMonth, premium);

        if (renewalMonth.canPaidBy(amountToRenew)) {
            period.addNewMonth(renewalMonth);
            return new MonthToPay(renewalMonth, amountToRenew);
        } else {
            throw new RenewalException("Payment amount is not enough to renew contract");
        }
    }

    protected abstract Amount getAmountToRenew(Period period, PaymentData paymentData);

}