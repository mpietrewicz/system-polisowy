package pl.mpietrewicz.sp.modules.balance.domain.balance.paymentpolicy;

import pl.mpietrewicz.sp.ddd.annotations.domain.DomainPolicy;
import pl.mpietrewicz.sp.modules.balance.domain.balance.MonthToPay;
import pl.mpietrewicz.sp.modules.balance.domain.balance.Period;
import pl.mpietrewicz.sp.modules.balance.domain.balance.operation.PaymentData;
import pl.mpietrewicz.sp.modules.balance.exceptions.RenewalException;

@DomainPolicy
public interface PaymentPolicy {

    MonthToPay getMonthToPay(Period period, PaymentData paymentData) throws RenewalException;

}