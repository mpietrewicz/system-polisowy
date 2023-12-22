package pl.mpietrewicz.sp.modules.balance.domain.balance.paymentpolicy;

import pl.mpietrewicz.sp.ddd.annotations.domain.DomainPolicy;
import pl.mpietrewicz.sp.modules.balance.domain.balance.month.Month;
import pl.mpietrewicz.sp.modules.balance.domain.balance.operation.PaymentData;

@DomainPolicy
public interface PaymentPolicy {

    Month getMonthToPay(Period period, PaymentData paymentData);

}