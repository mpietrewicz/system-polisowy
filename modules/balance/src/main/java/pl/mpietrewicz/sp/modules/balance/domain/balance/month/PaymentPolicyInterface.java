package pl.mpietrewicz.sp.modules.balance.domain.balance.month;

import pl.mpietrewicz.sp.ddd.annotations.domain.DomainPolicy;
import pl.mpietrewicz.sp.modules.balance.domain.balance.Period;

import java.time.LocalDate;

@DomainPolicy
public interface PaymentPolicyInterface {

    Month getFirstMonthToPay(Period period, LocalDate paymentDate);

}