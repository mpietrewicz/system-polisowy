package pl.mpietrewicz.sp.modules.balance.domain.balance.policy.payment;

import pl.mpietrewicz.sp.ddd.annotations.domain.DomainPolicyImpl;
import pl.mpietrewicz.sp.ddd.canonicalmodel.publishedlanguage.snapshot.premium.PremiumSnapshot;
import pl.mpietrewicz.sp.ddd.sharedkernel.valueobject.Amount;
import pl.mpietrewicz.sp.modules.balance.domain.balance.Period;
import pl.mpietrewicz.sp.modules.balance.domain.balance.month.LastMonth;
import pl.mpietrewicz.sp.modules.balance.domain.balance.month.MonthFactory;
import pl.mpietrewicz.sp.modules.balance.domain.balance.policy.refund.RefundUnderpaymentPolicy;
import pl.mpietrewicz.sp.modules.balance.exceptions.RenewalException;

import java.time.LocalDate;
import java.time.YearMonth;

import static pl.mpietrewicz.sp.DateUtils.getMonthsBetween;

@DomainPolicyImpl
public class RenewalWithUnderpaymentPolicy implements PaymentPolicy {

    private final PremiumSnapshot premiumSnapshot;

    private final int graceMonths;

    protected RenewalWithUnderpaymentPolicy(PremiumSnapshot premiumSnapshot, int graceMonths) {
        this.premiumSnapshot = premiumSnapshot;
        this.graceMonths = graceMonths;
    }

    @Override
    public void pay(Period period, LocalDate date, Amount amount) throws RenewalException {
        MonthToPay monthToPay = getMonthToPay(period, date, amount);
        period.pay(monthToPay.month, monthToPay.amount, premiumSnapshot);
    }

    private MonthToPay getMonthToPay(Period period, LocalDate date, Amount amount) throws RenewalException {
        YearMonth lastPaidMonth = period.getLastPaidYearMonth();
        YearMonth paymentMonth = YearMonth.from(date);

        if (getMonthsBetween(lastPaidMonth, paymentMonth) <= graceMonths) {
            LastMonth lastMonth = period.getLastMonth()
                    .orElseGet(() -> period.createFirstMonth(premiumSnapshot));
            return new MonthToPay(lastMonth, amount);
        } else {
            RefundUnderpaymentPolicy refundPolicy = new RefundUnderpaymentPolicy();
            Amount underpayment = refundPolicy.refund(period);
            Amount amountToRenew = amount.add(underpayment);
            return tryCreateRenewalMonth(date, amountToRenew);
        }
    }

    private MonthToPay tryCreateRenewalMonth(LocalDate date, Amount amount) throws RenewalException {
        YearMonth paymentMonth = YearMonth.from(date);
        LastMonth renewalMonth = MonthFactory.create(paymentMonth, premiumSnapshot, true);

        if (renewalMonth.canPaidBy(amount)) {
            return new MonthToPay(renewalMonth, amount);
        } else {
            throw new RenewalException("Payment amount is not enough to renew contract");
        }
    }

    private static class MonthToPay {
        private final LastMonth month;
        private final Amount amount;

        public MonthToPay(LastMonth month, Amount amount) {
            this.month = month;
            this.amount = amount;
        }
    }

}