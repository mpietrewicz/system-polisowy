package pl.mpietrewicz.sp.modules.oldbalance.domain.balance.policy.periodcover;

import pl.mpietrewicz.sp.modules.oldbalance.domain.balance.Periods;
import pl.mpietrewicz.sp.modules.oldbalance.domain.balance.operation.Operation;
import pl.mpietrewicz.sp.modules.oldbalance.domain.balance.operation.Refund;
import pl.mpietrewicz.sp.modules.oldbalance.domain.balance.period.Period;
import pl.mpietrewicz.sp.modules.oldbalance.domain.balance.policy.PeriodCoverPolicy;
import pl.mpietrewicz.sp.ddd.annotations.domain.ValueObject;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import java.time.YearMonth;

import static pl.mpietrewicz.sp.modules.oldbalance.domain.balance.period.Period.DESCENDING;

//@DomainPolicyImpl
@ValueObject
@Entity
@DiscriminatorValue("WITHOUT_RENEWAL")
public class WithoutRenewal extends PeriodCoverPolicy {

    @Override
    public Period getFirstPeriodToPay(Periods periods, Operation operation) {
        Period periodAtPayment = periods.getAt(YearMonth.from(operation.getDate()))
                .orElseThrow(() -> new RuntimeException("No period at payment month!"));
        if (periodAtPayment.isPaid()) {
            return periodAtPayment;
        } else {
            throw new RuntimeException("Can't add payment at unpaid period!");
        }
    }

    @Override
    public Period getLastPeriodToRefund(Periods periods, Refund refund) {
        return periods.getCovered().stream()
                .min(DESCENDING)
                .orElseThrow(() -> new RuntimeException("No periods to refund!"));
    }

}