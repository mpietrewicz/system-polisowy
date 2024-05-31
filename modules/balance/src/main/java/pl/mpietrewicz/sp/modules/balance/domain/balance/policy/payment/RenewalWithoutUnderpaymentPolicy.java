package pl.mpietrewicz.sp.modules.balance.domain.balance.policy.payment;

import pl.mpietrewicz.sp.ddd.annotations.domain.DomainPolicyImpl;
import pl.mpietrewicz.sp.ddd.canonicalmodel.publishedlanguage.snapshot.premium.PremiumSnapshot;
import pl.mpietrewicz.sp.ddd.sharedkernel.valueobject.PositiveAmount;
import pl.mpietrewicz.sp.modules.balance.domain.balance.period.Period;
import pl.mpietrewicz.sp.modules.balance.domain.balance.month.LastMonth;
import pl.mpietrewicz.sp.modules.balance.domain.balance.month.MonthFactory;
import pl.mpietrewicz.sp.modules.balance.exceptions.RenewalException;

import java.time.LocalDate;
import java.time.YearMonth;

import static pl.mpietrewicz.sp.ddd.sharedkernel.util.DateUtils.getMonthsBetween;

@DomainPolicyImpl
public class RenewalWithoutUnderpaymentPolicy implements PaymentPolicy {

    private final PremiumSnapshot premiumSnapshot;

    private final int graceMonths;

    protected RenewalWithoutUnderpaymentPolicy(PremiumSnapshot premiumSnapshot, int graceMonths) {
        this.premiumSnapshot = premiumSnapshot;
        this.graceMonths = graceMonths;
    }

    @Override
    public void pay(Period period, LocalDate date, PositiveAmount payment) throws RenewalException {
        LastMonth monthToPay = getMonthToPay(period, date, payment);
        period.pay(monthToPay, payment, premiumSnapshot);
    }

    private LastMonth getMonthToPay(Period period, LocalDate date, PositiveAmount payment) throws RenewalException {
        YearMonth lastPaidMonth = period.getLastPaidYearMonth();
        YearMonth paymentMonth = YearMonth.from(date);

        if (getMonthsBetween(lastPaidMonth, paymentMonth) <= graceMonths) {
            return period.getLastMonth()
                    .orElseGet(() ->  period.createFirstMonth(premiumSnapshot));
        } else {
            return tryCreateRenewalMonth(date, payment);
        }
    }

    private LastMonth tryCreateRenewalMonth(LocalDate date, PositiveAmount payment) throws RenewalException {
        YearMonth paymentMonth = YearMonth.from(date);
        LastMonth renewalMonth = MonthFactory.create(paymentMonth, premiumSnapshot, true);

        if (renewalMonth.canPaidBy(payment)) {
            return renewalMonth;
        } else {
            throw new RenewalException("Payment amount is not enough to renew contract");
        }
    }

}