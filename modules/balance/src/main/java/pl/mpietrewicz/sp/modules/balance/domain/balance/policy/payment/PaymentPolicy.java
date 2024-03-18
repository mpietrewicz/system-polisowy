package pl.mpietrewicz.sp.modules.balance.domain.balance.policy.payment;

import pl.mpietrewicz.sp.ddd.annotations.domain.DomainPolicy;
import pl.mpietrewicz.sp.ddd.sharedkernel.valueobject.Amount;
import pl.mpietrewicz.sp.modules.balance.domain.balance.Period;
import pl.mpietrewicz.sp.modules.balance.exceptions.RenewalException;

import java.time.LocalDate;

@DomainPolicy
public interface PaymentPolicy {

    void pay(Period period, LocalDate date, Amount amount) throws RenewalException;

}