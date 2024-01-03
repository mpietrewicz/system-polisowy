package pl.mpietrewicz.sp.modules.balance.domain.balance.paymentpolicy;


import pl.mpietrewicz.sp.ddd.annotations.domain.DomainPolicyFactory;
import pl.mpietrewicz.sp.ddd.canonicalmodel.publishedlanguage.PaymentPolicyEnum;
import pl.mpietrewicz.sp.modules.balance.domain.balance.Premium;
import pl.mpietrewicz.sp.modules.balance.domain.balance.month.ComponentPremium;

import java.util.List;

@DomainPolicyFactory
public class PaymentPolicyFactory {

    public static PaymentPolicy create(PaymentPolicyEnum policy, Premium premium) {
        ContinuationPolicy continuationPolicy = new ContinuationPolicy();

        if (policy == PaymentPolicyEnum.CONTINUATION) {
            return continuationPolicy;
        } else if (policy == PaymentPolicyEnum.RENEWAL) {
            return new RenewalPolicy(continuationPolicy, premium);
        } else if (policy == PaymentPolicyEnum.NO_RENEWAL) {
            return new NoRenewalPolicy(continuationPolicy);
        } else {
            throw new IllegalArgumentException();
        }
    }

}