package pl.mpietrewicz.sp.modules.oldbalance.domain.balance.policy.periodcover;

import pl.mpietrewicz.sp.modules.oldbalance.domain.balance.policy.PeriodCoverPolicy;
import pl.mpietrewicz.sp.ddd.annotations.domain.DomainPolicyFactory;

import static pl.mpietrewicz.sp.modules.oldbalance.domain.balance.policy.periodcover.PeriodCoverPolicyEnum.WITHOUT_LIMITS;
import static pl.mpietrewicz.sp.modules.oldbalance.domain.balance.policy.periodcover.PeriodCoverPolicyEnum.WITHOUT_RENEWAL;
import static pl.mpietrewicz.sp.modules.oldbalance.domain.balance.policy.periodcover.PeriodCoverPolicyEnum.WITH_RENEWAL;

@DomainPolicyFactory
public class PeriodCoverPolicyFactory {

    public PeriodCoverPolicy create(PeriodCoverPolicyEnum type) {
        if (type == WITHOUT_LIMITS) {
            return new WithoutLimits();
        } else if (type == WITHOUT_RENEWAL) {
            return new WithoutRenewal();
        } else if (type == WITH_RENEWAL) {
            return new WithRenewal();
        } else {
            throw new IllegalArgumentException();
        }
    }

}