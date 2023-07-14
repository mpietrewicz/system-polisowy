package pl.mpietrewicz.sp.modules.oldbalance.domain.balance;

import lombok.Builder;
import lombok.Getter;
import pl.mpietrewicz.sp.modules.oldbalance.domain.balance.policy.operationcalculation.PaymentCalculationPolicyEnum;
import pl.mpietrewicz.sp.modules.oldbalance.domain.balance.policy.periodcover.PeriodCoverPolicyEnum;

@Builder
@Getter
public class BalancePolicies {

    private final PeriodCoverPolicyEnum periodCoverPolicyEnum;
    private final PaymentCalculationPolicyEnum paymentCalculationPolicyEnum;

}