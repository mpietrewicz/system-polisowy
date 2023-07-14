package pl.mpietrewicz.sp.modules.oldbalance.domain.balance.policy.operationcalculation;

import pl.mpietrewicz.sp.modules.oldbalance.domain.balance.policy.OperationCalculationPolicy;
import pl.mpietrewicz.sp.ddd.annotations.domain.DomainPolicyFactory;

import static pl.mpietrewicz.sp.modules.oldbalance.domain.balance.policy.operationcalculation.PaymentCalculationPolicyEnum.COMPLEX;
import static pl.mpietrewicz.sp.modules.oldbalance.domain.balance.policy.operationcalculation.PaymentCalculationPolicyEnum.ERASE;
import static pl.mpietrewicz.sp.modules.oldbalance.domain.balance.policy.operationcalculation.PaymentCalculationPolicyEnum.SIMPLE;

@DomainPolicyFactory
public class PaymentCalculationPolicyFactory {

    public static OperationCalculationPolicy create(PaymentCalculationPolicyEnum type) {
        if (type == COMPLEX) {
            return new RecalculatePolicy();
        } else if (type == ERASE) {
            return new ErasePolicy();
        } else if ((type == SIMPLE)) {
            return new SimplePolicy();
        } else {
            throw new IllegalArgumentException();
        }
    }

}