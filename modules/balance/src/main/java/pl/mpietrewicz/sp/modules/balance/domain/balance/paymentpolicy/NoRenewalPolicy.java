package pl.mpietrewicz.sp.modules.balance.domain.balance.paymentpolicy;

import pl.mpietrewicz.sp.ddd.annotations.domain.DomainPolicyImpl;
import pl.mpietrewicz.sp.modules.balance.domain.balance.MonthToPay;
import pl.mpietrewicz.sp.modules.balance.domain.balance.Period;
import pl.mpietrewicz.sp.modules.balance.domain.balance.operation.PaymentData;
import pl.mpietrewicz.sp.modules.balance.exceptions.RenewalException;

import java.time.YearMonth;

@DomainPolicyImpl
public class NoRenewalPolicy implements PaymentPolicy {

    private final ContinuationPolicy continuationPolicy;

    public NoRenewalPolicy(ContinuationPolicy continuationPolicy) {
        this.continuationPolicy = continuationPolicy;
    }

    @Override
    public MonthToPay getMonthToPay(Period period, PaymentData paymentData) throws RenewalException {
        if (period.getMonthOf(YearMonth.from(paymentData.getDate())).isPresent()) {
            return continuationPolicy.getMonthToPay(period, paymentData);
        } else {
            throw new RenewalException("Current payment policy not support contract renew");
        }
    }

}