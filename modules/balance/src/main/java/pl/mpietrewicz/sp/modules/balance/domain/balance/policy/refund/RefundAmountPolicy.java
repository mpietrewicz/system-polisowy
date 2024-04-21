package pl.mpietrewicz.sp.modules.balance.domain.balance.policy.refund;

import pl.mpietrewicz.sp.ddd.annotations.domain.DomainPolicyImpl;
import pl.mpietrewicz.sp.ddd.sharedkernel.Amount;
import pl.mpietrewicz.sp.modules.balance.domain.balance.Period;
import pl.mpietrewicz.sp.modules.balance.domain.balance.month.LastMonth;
import pl.mpietrewicz.sp.modules.balance.exceptions.RefundException;

@DomainPolicyImpl
public class RefundAmountPolicy {

    public void refund(Period period, Amount amount) throws RefundException {
        LastMonth lastMonth = period.getLastMonth()
                .orElseThrow(() -> new RefundException("No months to refund"));
        period.refund(lastMonth, amount);
    }

}