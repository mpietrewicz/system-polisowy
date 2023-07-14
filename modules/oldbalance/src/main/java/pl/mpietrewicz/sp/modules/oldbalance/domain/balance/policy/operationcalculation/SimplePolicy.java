package pl.mpietrewicz.sp.modules.oldbalance.domain.balance.policy.operationcalculation;

import lombok.RequiredArgsConstructor;
import pl.mpietrewicz.sp.modules.oldbalance.domain.balance.Operations;
import pl.mpietrewicz.sp.modules.oldbalance.domain.balance.Periods;
import pl.mpietrewicz.sp.modules.oldbalance.domain.balance.operation.Payment;
import pl.mpietrewicz.sp.modules.oldbalance.domain.balance.operation.Refund;
import pl.mpietrewicz.sp.modules.oldbalance.domain.balance.period.Period;
import pl.mpietrewicz.sp.modules.oldbalance.domain.balance.policy.OperationCalculationPolicy;
import pl.mpietrewicz.sp.ddd.annotations.domain.DomainPolicyImpl;

@DomainPolicyImpl
@RequiredArgsConstructor
public class SimplePolicy implements OperationCalculationPolicy {

    public void calculate(Payment payment, Operations operations, Periods periods) {
        Period period = periods.getFirstPeriodToPay(payment);
        period.tryPay(payment.getAmount());
    }

    @Override
    public void calculate(Refund refund, Operations operations, Periods periods) {
        Period lastPeriodToRefundPart = periods.getLastPeriodToRefund(refund);;
        lastPeriodToRefundPart.tryRefund(refund.getAmount());
    }

}