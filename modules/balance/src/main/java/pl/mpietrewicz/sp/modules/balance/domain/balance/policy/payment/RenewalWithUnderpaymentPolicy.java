package pl.mpietrewicz.sp.modules.balance.domain.balance.policy.payment;

import pl.mpietrewicz.sp.ddd.annotations.domain.DomainPolicyImpl;
import pl.mpietrewicz.sp.ddd.canonicalmodel.publishedlanguage.snapshot.premium.PremiumSnapshot;
import pl.mpietrewicz.sp.ddd.sharedkernel.valueobject.Amount;
import pl.mpietrewicz.sp.ddd.sharedkernel.valueobject.PositiveAmount;
import pl.mpietrewicz.sp.modules.balance.domain.balance.month.LastMonth;
import pl.mpietrewicz.sp.modules.balance.domain.balance.month.MonthFactory;
import pl.mpietrewicz.sp.modules.balance.domain.balance.period.Period;
import pl.mpietrewicz.sp.modules.balance.domain.balance.policy.refund.RefundUnderpaymentPolicy;
import pl.mpietrewicz.sp.modules.balance.exceptions.RenewalException;

import java.time.LocalDate;
import java.time.YearMonth;

import static pl.mpietrewicz.sp.ddd.sharedkernel.util.DateUtils.getMonthsBetween;

@DomainPolicyImpl
public class RenewalWithUnderpaymentPolicy implements PaymentPolicy {

    private final PremiumSnapshot premiumSnapshot;

    private final int graceMonths;

    protected RenewalWithUnderpaymentPolicy(PremiumSnapshot premiumSnapshot, int graceMonths) {
        this.premiumSnapshot = premiumSnapshot;
        this.graceMonths = graceMonths;
    }

    @Override
    public void pay(Period period, LocalDate date, PositiveAmount payment) throws RenewalException {
        MonthToPay monthToPay = getMonthToPay(period, date, payment);
        period.pay(monthToPay.month, monthToPay.payment, premiumSnapshot);
    }

    private MonthToPay getMonthToPay(Period period, LocalDate date, PositiveAmount payment) throws RenewalException {
        YearMonth lastPaidMonth = period.getLastPaidYearMonth();
        YearMonth paymentMonth = YearMonth.from(date);

        if (getMonthsBetween(lastPaidMonth, paymentMonth) <= graceMonths) {
            LastMonth lastMonth = period.getLastMonth()
                    .orElseGet(() -> period.createFirstMonth(premiumSnapshot));
            return new MonthToPay(lastMonth, payment);
        } else {
            PositiveAmount paymentToRenew = getPaymentToRenew(period, payment);
            return tryCreateRenewalMonth(date, paymentToRenew);
        }
    }

    private MonthToPay tryCreateRenewalMonth(LocalDate date, PositiveAmount paymentToRenew) throws RenewalException {
        YearMonth paymentMonth = YearMonth.from(date);
        LastMonth renewalMonth = MonthFactory.create(paymentMonth, premiumSnapshot, true);

        if (renewalMonth.canPaidBy(paymentToRenew)) {
            return new MonthToPay(renewalMonth, paymentToRenew);
        } else {
            throw new RenewalException("Payment amount is not enough to renew contract");
        }
    }

    private PositiveAmount getPaymentToRenew(Period period, PositiveAmount payment) {
        Amount underpayment = new RefundUnderpaymentPolicy().refund(period);
        return underpayment.isPositive()
                ? payment.add((PositiveAmount) underpayment)
                : payment;
    }

    private static class MonthToPay {
        private final LastMonth month;
        private final PositiveAmount payment;

        public MonthToPay(LastMonth month, PositiveAmount payment) {
            this.month = month;
            this.payment = payment;
        }
    }

}