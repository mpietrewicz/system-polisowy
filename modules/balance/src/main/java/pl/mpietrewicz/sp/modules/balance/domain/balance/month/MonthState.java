package pl.mpietrewicz.sp.modules.balance.domain.balance.month;

import pl.mpietrewicz.sp.ddd.sharedkernel.Amount;
import pl.mpietrewicz.sp.ddd.sharedkernel.PositiveAmount;

public interface MonthState {

    Amount pay(Month month, PositiveAmount payment);

    Amount refund(Month month, PositiveAmount refund);

    boolean canPaidBy(Month month, Amount payment);

    PaidStatus getPaidStatus();

    Amount getPaid();

    boolean isPaid();

    boolean hasPayment();

}