package pl.mpietrewicz.sp.modules.oldbalance.domain.balance.policy.operationcalculation;

import lombok.RequiredArgsConstructor;
import pl.mpietrewicz.sp.modules.oldbalance.domain.balance.Operations;
import pl.mpietrewicz.sp.modules.oldbalance.domain.balance.Periods;
import pl.mpietrewicz.sp.modules.oldbalance.domain.balance.operation.Operation;
import pl.mpietrewicz.sp.modules.oldbalance.domain.balance.operation.Payment;
import pl.mpietrewicz.sp.modules.oldbalance.domain.balance.operation.Refund;
import pl.mpietrewicz.sp.modules.oldbalance.domain.balance.period.Period;
import pl.mpietrewicz.sp.modules.oldbalance.domain.balance.period.PeriodSnapshot;
import pl.mpietrewicz.sp.modules.oldbalance.domain.balance.period.PeriodState;
import pl.mpietrewicz.sp.modules.oldbalance.domain.balance.period.PeriodStateFactory;
import pl.mpietrewicz.sp.modules.oldbalance.domain.balance.policy.OperationCalculationPolicy;
import pl.mpietrewicz.sp.ddd.annotations.domain.DomainPolicyImpl;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@DomainPolicyImpl
@RequiredArgsConstructor
public class RecalculatePolicy implements OperationCalculationPolicy {

    public void calculate(Payment payment, Operations operations, Periods periods) {

        Optional<Payment> previousFund = operations.getPaymentBefore(payment);
        if (previousFund.isPresent()) {
            PeriodSnapshot snapshot = previousFund.get().getLastPeriodSnapshot();
            restorePeriodFrom(snapshot, periods);
        } else {
            periods.eraseState();
        }

        List<Operation> fundsToRecalculate = new ArrayList<>();
        fundsToRecalculate.add(payment);
        fundsToRecalculate.addAll(operations.getSortedOperationsAfter(payment));
        for (Operation operation : fundsToRecalculate) {
            Period period = periods.getFirstPeriodToPay(operation);
            period.tryPay(operation.getAmount());
        }
    }

    @Override
    public void calculate(Refund refund, Operations operations, Periods periods) {
        // todo: dokończyć impelemtacje
    }

    private void restorePeriodFrom(PeriodSnapshot snapshot, Periods periods) {
        Period period = periods.getAt(snapshot.getMonth()).orElseThrow();
        PeriodState state = PeriodStateFactory.createState(period, snapshot.getStatus(),
                snapshot.getUnderpayment(), snapshot.getOverpayment());
        period.changeState(state);
        periods.eraseStateAfter(snapshot.getMonth());
    }

}