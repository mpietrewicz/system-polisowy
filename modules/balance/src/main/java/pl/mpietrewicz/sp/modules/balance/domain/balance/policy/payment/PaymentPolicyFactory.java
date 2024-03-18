package pl.mpietrewicz.sp.modules.balance.domain.balance.policy.payment;


import pl.mpietrewicz.sp.ddd.annotations.domain.DomainPolicyFactory;
import pl.mpietrewicz.sp.ddd.canonicalmodel.publishedlanguage.PaymentPolicyEnum;
import pl.mpietrewicz.sp.ddd.canonicalmodel.publishedlanguage.snapshot.premium.PremiumSnapshot;

@DomainPolicyFactory
public class PaymentPolicyFactory {

    public static PaymentPolicy create(PaymentPolicyEnum policy, PremiumSnapshot premiumSnapshot) {
        ContinuationPolicy continuationPolicy = new ContinuationPolicy(premiumSnapshot);

        if (policy == PaymentPolicyEnum.CONTINUATION) {
            return continuationPolicy;
        } else if (policy == PaymentPolicyEnum.RENEWAL_WITH_UNDERPAYMENT) {
            return new RenewalWithUnderpaymentPolicy(premiumSnapshot, policy.getGraceMonths());
        } else if (policy == PaymentPolicyEnum.RENEWAL_WITHOUT_UNDERPAYMENT) {
            return new RenewalWithoutUnderpaymentPolicy(premiumSnapshot, policy.getGraceMonths());
        } else if (policy == PaymentPolicyEnum.NO_RENEWAL) {
            return new NoRenewalPolicy(premiumSnapshot);
        } else {
            throw new IllegalArgumentException();
        }
    }

}