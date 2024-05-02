package pl.mpietrewicz.sp.modules.balance.domain.balance.policy.payment;

import pl.mpietrewicz.sp.ddd.annotations.domain.DomainPolicyImpl;
import pl.mpietrewicz.sp.ddd.canonicalmodel.publishedlanguage.snapshot.premium.PremiumSnapshot;
import pl.mpietrewicz.sp.ddd.sharedkernel.valueobject.PositiveAmount;
import pl.mpietrewicz.sp.modules.balance.domain.balance.period.Period;
import pl.mpietrewicz.sp.modules.balance.domain.balance.month.LastMonth;
import pl.mpietrewicz.sp.modules.balance.exceptions.RenewalException;

import java.time.LocalDate;
import java.time.YearMonth;

@DomainPolicyImpl
public class NoRenewalPolicy implements PaymentPolicy {

    private final PremiumSnapshot premiumSnapshot;

    protected NoRenewalPolicy(PremiumSnapshot premiumSnapshot) {
        this.premiumSnapshot = premiumSnapshot;
    }

    @Override
    public void pay(Period period, LocalDate date, PositiveAmount payment) throws RenewalException {
        LastMonth monthToPay = getMonthToPay(period, date);
        period.pay(monthToPay, payment, premiumSnapshot);
    }

    private LastMonth getMonthToPay(Period period, LocalDate date) throws RenewalException {
        if (period.has(YearMonth.from(date))) {
            return period.getLastMonth()
                    .orElseGet(() -> period.createFirstMonth(premiumSnapshot));
        } else {
            throw new RenewalException("Current payment policy not support contract renew");
        }
    }

}