package pl.mpietrewicz.sp.modules.balance.domain.balance.paymentpolicy;

import pl.mpietrewicz.sp.ddd.annotations.domain.DomainPolicyImpl;
import pl.mpietrewicz.sp.modules.balance.domain.balance.month.Month;
import pl.mpietrewicz.sp.modules.balance.domain.balance.operation.PaymentData;

@DomainPolicyImpl
public class ContinuationPolicy implements PaymentPolicy {

    @Override
    public Month getMonthToPay(Period period, PaymentData paymentData) {
        return period.getLastPaidMonth()
                .map(lastPaid -> period.getNextMonth(lastPaid)
                        .orElse(lastPaid))
                .orElseGet(period::getFirstMonth);
    }

}