package pl.mpietrewicz.sp.modules.balance.domain.balance.paymentpolicy.renewal;

import pl.mpietrewicz.sp.ddd.annotations.domain.DomainPolicyImpl;
import pl.mpietrewicz.sp.ddd.canonicalmodel.publishedlanguage.snapshot.premium.PremiumSnapshot;
import pl.mpietrewicz.sp.ddd.sharedkernel.Amount;
import pl.mpietrewicz.sp.modules.balance.domain.balance.operation.PaymentData;
import pl.mpietrewicz.sp.modules.balance.domain.balance.paymentpolicy.PaymentPolicy;
import pl.mpietrewicz.sp.modules.balance.domain.balance.Period;

@DomainPolicyImpl
public class WithoutUnderpaymentPolicy extends RenewalPolicy {

    public WithoutUnderpaymentPolicy(PaymentPolicy continuationPolicy, PremiumSnapshot premiumSnapshot) {
        super(continuationPolicy, premiumSnapshot);
    }

    @Override
    protected Amount getAmountToRenew(Period period, PaymentData paymentData) {
        return paymentData.getAmount();
    }

}