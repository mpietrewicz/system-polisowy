package pl.mpietrewicz.sp.modules.balance.domain.balance.month.paymentpolicy;


import pl.mpietrewicz.sp.ddd.annotations.domain.DomainPolicyFactory;
import pl.mpietrewicz.sp.ddd.sharedkernel.PaymentPolicyEnum;
import pl.mpietrewicz.sp.modules.balance.domain.balance.month.PaymentPolicy;

@DomainPolicyFactory
public class PaymentPolicyFactory {

    public PaymentPolicy create(PaymentPolicyEnum policy) {
        if (policy == PaymentPolicyEnum.WITHOUT_LIMITS) {
            return new WithoutLimits();
        } else if (policy == PaymentPolicyEnum.WITH_RENEWAL) {
            return new WithRenewal();
        } else if (policy == PaymentPolicyEnum.WITHOUT_RENEWAL) {
            return new WithoutRenewal();
        } else {
            throw new IllegalArgumentException();
        }
    }

}