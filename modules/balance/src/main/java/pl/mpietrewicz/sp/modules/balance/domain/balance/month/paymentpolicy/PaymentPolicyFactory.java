package pl.mpietrewicz.sp.modules.balance.domain.balance.month.paymentpolicy;


import pl.mpietrewicz.sp.ddd.annotations.domain.DomainPolicyFactory;
import pl.mpietrewicz.sp.ddd.canonicalmodel.publishedlanguage.PaymentPolicy;
import pl.mpietrewicz.sp.modules.balance.domain.balance.month.PaymentPolicyInterface;

@DomainPolicyFactory
public class PaymentPolicyFactory {

    public PaymentPolicyInterface create(PaymentPolicy policy) {
        if (policy == PaymentPolicy.WITHOUT_LIMITS) {
            return new WithoutLimits();
        } else if (policy == PaymentPolicy.WITH_RENEWAL) {
            return new WithRenewal();
        } else if (policy == PaymentPolicy.WITHOUT_RENEWAL) {
            return new WithoutRenewal();
        } else {
            throw new IllegalArgumentException();
        }
    }

}