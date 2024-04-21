package pl.mpietrewicz.sp.modules.balance.domain.balance.policy.payment;

import pl.mpietrewicz.sp.ddd.annotations.domain.DomainPolicyImpl;
import pl.mpietrewicz.sp.ddd.canonicalmodel.publishedlanguage.snapshot.premium.PremiumSnapshot;
import pl.mpietrewicz.sp.ddd.sharedkernel.Amount;
import pl.mpietrewicz.sp.modules.balance.domain.balance.Period;
import pl.mpietrewicz.sp.modules.balance.domain.balance.month.LastMonth;
import pl.mpietrewicz.sp.modules.balance.exceptions.RenewalException;

import java.time.LocalDate;

@DomainPolicyImpl
public class ContinuationPolicy implements PaymentPolicy {

    private final PremiumSnapshot premiumSnapshot;

    protected ContinuationPolicy(PremiumSnapshot premiumSnapshot) {
        this.premiumSnapshot = premiumSnapshot;
    }

    @Override
    public void pay(Period period, LocalDate date, Amount amount) throws RenewalException {
        LastMonth lastMonth = period.getLastMonth()
                .orElseGet(() -> period.createFirstMonth(premiumSnapshot));
        period.pay(lastMonth, amount, premiumSnapshot);
    }

}