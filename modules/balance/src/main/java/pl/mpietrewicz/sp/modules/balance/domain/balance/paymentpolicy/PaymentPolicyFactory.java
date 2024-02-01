package pl.mpietrewicz.sp.modules.balance.domain.balance.paymentpolicy;


import pl.mpietrewicz.sp.ddd.annotations.domain.DomainPolicyFactory;
import pl.mpietrewicz.sp.ddd.canonicalmodel.publishedlanguage.PaymentPolicyEnum;
import pl.mpietrewicz.sp.ddd.canonicalmodel.publishedlanguage.snapshot.premium.PremiumSnapshot;

@DomainPolicyFactory
public class PaymentPolicyFactory {

    public static PaymentPolicy create(PaymentPolicyEnum policy, PremiumSnapshot premiumSnapshot) {
        ContinuationPolicy continuationPolicy = new ContinuationPolicy(premiumSnapshot);

        if (policy == PaymentPolicyEnum.CONTINUATION) {
            return continuationPolicy;
        } else if (policy == PaymentPolicyEnum.RENEWAL) {
            return new RenewalPolicy(continuationPolicy, premiumSnapshot);
        } else if (policy == PaymentPolicyEnum.NO_RENEWAL) {
            return new NoRenewalPolicy(continuationPolicy);
        } else {
            throw new IllegalArgumentException();
        }
    }

}