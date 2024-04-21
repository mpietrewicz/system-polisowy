package pl.mpietrewicz.sp.modules.balance.domain.balance.policy.refund;

import pl.mpietrewicz.sp.ddd.annotations.domain.DomainPolicyImpl;
import pl.mpietrewicz.sp.ddd.sharedkernel.Amount;
import pl.mpietrewicz.sp.modules.balance.domain.balance.Period;
import pl.mpietrewicz.sp.modules.balance.domain.balance.month.LastMonth;

import java.util.Optional;

import static pl.mpietrewicz.sp.ddd.sharedkernel.Amount.ZERO;

@DomainPolicyImpl
public class RefundUnderpaymentPolicy {

    public Amount refund(Period period) {
        Optional<LastMonth> lastMonth = period.getLastMonth();
        if (lastMonth.isPresent()) {
            return period.refund(lastMonth.get());
        } else {
            return ZERO;
        }
    }

}