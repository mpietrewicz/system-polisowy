package pl.mpietrewicz.sp.modules.balance.domain.balance.policy.refund;

import pl.mpietrewicz.sp.ddd.annotations.domain.DomainPolicyImpl;
import pl.mpietrewicz.sp.ddd.sharedkernel.valueobject.PositiveAmount;
import pl.mpietrewicz.sp.modules.balance.domain.balance.period.Period;
import pl.mpietrewicz.sp.modules.balance.domain.balance.month.LastMonth;
import pl.mpietrewicz.sp.modules.balance.exceptions.NoMonthsToRefundException;
import pl.mpietrewicz.sp.modules.balance.exceptions.RefundException;

@DomainPolicyImpl
public class RefundAmountPolicy {

    public void refund(Period period, PositiveAmount refund) throws RefundException {
        LastMonth lastMonth = period.getLastMonth()
                .orElseThrow(() -> new NoMonthsToRefundException("No months to refund"));
        period.refundAmount(lastMonth, refund);
    }

}