package pl.mpietrewicz.sp.modules.oldbalance.domain.balance.policy.operationcalculation;

import lombok.RequiredArgsConstructor;
import pl.mpietrewicz.sp.modules.oldbalance.domain.balance.Operations;
import pl.mpietrewicz.sp.modules.oldbalance.domain.balance.Periods;
import pl.mpietrewicz.sp.modules.oldbalance.domain.balance.operation.Operation;
import pl.mpietrewicz.sp.modules.oldbalance.domain.balance.operation.Payment;
import pl.mpietrewicz.sp.modules.oldbalance.domain.balance.operation.Refund;
import pl.mpietrewicz.sp.modules.oldbalance.domain.balance.period.Period;
import pl.mpietrewicz.sp.modules.oldbalance.domain.balance.policy.OperationCalculationPolicy;
import pl.mpietrewicz.sp.ddd.annotations.domain.DomainPolicyImpl;

@DomainPolicyImpl
@RequiredArgsConstructor
public class ErasePolicy implements OperationCalculationPolicy {

    public void calculate(Payment newPayment, Operations operations, Periods periods) {
        eraseAllPeriodsStates(periods);
        for (Operation operation : operations.getAll()) {
            Period period = periods.getFirstPeriodToPay(operation);
            period.tryPay(operation.getAmount());
        }
    }

    @Override
    public void calculate(Refund refund, Operations operations, Periods periods) {

    }

    private void eraseAllPeriodsStates(Periods periods) {
        periods.eraseState();
    }

}