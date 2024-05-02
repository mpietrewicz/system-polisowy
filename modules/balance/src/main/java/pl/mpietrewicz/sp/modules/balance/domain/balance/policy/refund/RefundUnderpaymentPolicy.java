package pl.mpietrewicz.sp.modules.balance.domain.balance.policy.refund;

import pl.mpietrewicz.sp.ddd.annotations.domain.DomainPolicyImpl;
import pl.mpietrewicz.sp.ddd.sharedkernel.valueobject.Amount;
import pl.mpietrewicz.sp.ddd.sharedkernel.valueobject.ZeroAmount;
import pl.mpietrewicz.sp.modules.balance.domain.balance.period.Period;
import pl.mpietrewicz.sp.modules.balance.domain.balance.month.LastMonth;

import java.util.Optional;

@DomainPolicyImpl
public class RefundUnderpaymentPolicy {

    public Amount refund(Period period) {
        Optional<LastMonth> lastMonth = period.getLastMonth();
        if (lastMonth.isPresent()) {
            return period.refundMonth(lastMonth.get());
        } else {
            return new ZeroAmount();
        }
    }

}