package pl.mpietrewicz.sp.modules.oldbalance.domain.balance.policy;

import pl.mpietrewicz.sp.modules.oldbalance.domain.balance.Operations;
import pl.mpietrewicz.sp.modules.oldbalance.domain.balance.Periods;
import pl.mpietrewicz.sp.modules.oldbalance.domain.balance.operation.Payment;
import pl.mpietrewicz.sp.modules.oldbalance.domain.balance.operation.Refund;
import pl.mpietrewicz.sp.ddd.annotations.domain.DomainPolicy;

@DomainPolicy
public interface OperationCalculationPolicy {

    void calculate(Payment payment, Operations operations, Periods periods);

    void calculate(Refund refund, Operations operations, Periods periods);

}