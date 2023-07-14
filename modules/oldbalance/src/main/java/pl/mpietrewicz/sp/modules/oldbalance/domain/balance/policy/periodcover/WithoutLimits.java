package pl.mpietrewicz.sp.modules.oldbalance.domain.balance.policy.periodcover;

import pl.mpietrewicz.sp.modules.oldbalance.domain.balance.Periods;
import pl.mpietrewicz.sp.modules.oldbalance.domain.balance.operation.Operation;
import pl.mpietrewicz.sp.modules.oldbalance.domain.balance.operation.Refund;
import pl.mpietrewicz.sp.modules.oldbalance.domain.balance.period.Period;
import pl.mpietrewicz.sp.modules.oldbalance.domain.balance.policy.PeriodCoverPolicy;
import pl.mpietrewicz.sp.ddd.annotations.domain.ValueObject;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import java.util.Comparator;
import java.util.Optional;

//@DomainPolicyImpl
@ValueObject
@Entity
@DiscriminatorValue("WITHOUT_LIMITS")
public class WithoutLimits extends PeriodCoverPolicy {

    @Override
    public Period getFirstPeriodToPay(Periods periods, Operation operation) {
        Optional<Period> firstNotPaidPediod = getFirstNotPaid(periods);
        Optional<Period> lastPaidPeriod = getLastPaid(periods);

        return firstNotPaidPediod
                .orElseGet(() -> lastPaidPeriod
                .orElseThrow(() -> new RuntimeException("No periods to paid!")));
    }

    @Override
    public Period getLastPeriodToRefund(Periods periods, Refund refund) {
        throw new IllegalStateException("Not implemented");
    }

    private Optional<Period> getFirstNotPaid(Periods periods) {
        return periods.getNotPaid().stream()
                .min(Comparator.comparing(Period::getMonth));
    }

    private Optional<Period> getLastPaid(Periods periods) {
        return periods.getPaid().stream()
                .max(Comparator.comparing(Period::getMonth));
    }

}