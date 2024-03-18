package pl.mpietrewicz.sp.modules.contract.domain.premium.policy;


import pl.mpietrewicz.sp.ddd.annotations.domain.DomainPolicyFactory;
import pl.mpietrewicz.sp.modules.contract.domain.premium.ChangePremiumPolicyEnum;

@DomainPolicyFactory
public class ChangePremiumPolicyFactory {

    public static ChangePremiumPolicy create(ChangePremiumPolicyEnum policy) {

        if (policy == ChangePremiumPolicyEnum.ONCE_PER_YEAR) {
            return new OnceChangePremiumPolicy();
        } else if (policy == ChangePremiumPolicyEnum.TWICE_PER_YEAR) {
            return new TwiceChangePremiumPolicy();
        } else if (policy == ChangePremiumPolicyEnum.EVERYTIME) {
            return new EverytimeChangePremiumPolicy();
        } else {
            throw new IllegalArgumentException();
        }
    }

}