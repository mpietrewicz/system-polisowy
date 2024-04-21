package pl.mpietrewicz.sp.modules.balance.domain.balance.policy.payment;

import pl.mpietrewicz.sp.ddd.annotations.domain.DomainPolicyImpl;
import pl.mpietrewicz.sp.ddd.canonicalmodel.publishedlanguage.snapshot.premium.PremiumSnapshot;
import pl.mpietrewicz.sp.ddd.sharedkernel.Amount;
import pl.mpietrewicz.sp.modules.balance.domain.balance.Period;
import pl.mpietrewicz.sp.modules.balance.domain.balance.month.LastMonth;
import pl.mpietrewicz.sp.modules.balance.domain.balance.month.MonthFactory;
import pl.mpietrewicz.sp.modules.balance.exceptions.RenewalException;

import java.time.LocalDate;
import java.time.YearMonth;

import static pl.mpietrewicz.sp.DateUtils.getMonthsBetween;

@DomainPolicyImpl
public class RenewalWithoutUnderpaymentPolicy implements PaymentPolicy {

    private final PremiumSnapshot premiumSnapshot;

    private final int graceMonths;

    protected RenewalWithoutUnderpaymentPolicy(PremiumSnapshot premiumSnapshot, int graceMonths) {
        this.premiumSnapshot = premiumSnapshot;
        this.graceMonths = graceMonths;
    }

    @Override
    public void pay(Period period, LocalDate date, Amount amount) throws RenewalException {
        LastMonth monthToPay = getMonthToPay(period, date, amount);
        period.pay(monthToPay, amount, premiumSnapshot);
    }

    private LastMonth getMonthToPay(Period period, LocalDate date, Amount amount) throws RenewalException {
        YearMonth lastPaidMonth = period.getLastPaidYearMonth();
        YearMonth paymentMonth = YearMonth.from(date);

        if (getMonthsBetween(lastPaidMonth, paymentMonth) <= graceMonths) {
            return period.getLastMonth()
                    .orElseGet(() ->  period.createFirstMonth(premiumSnapshot));
        } else {
            return tryCreateRenewalMonth(date, amount);
        }
    }

    private LastMonth tryCreateRenewalMonth(LocalDate date, Amount amount) throws RenewalException {
        YearMonth paymentMonth = YearMonth.from(date);
        LastMonth renewalMonth = MonthFactory.create(paymentMonth, premiumSnapshot, true);

        if (renewalMonth.canPaidBy(amount)) {
            return renewalMonth;
        } else {
            throw new RenewalException("Payment amount is not enough to renew contract");
        }
    }

}