package pl.mpietrewicz.sp.modules.balance.domain.balance.month;

import pl.mpietrewicz.sp.ddd.sharedkernel.Amount;
import pl.mpietrewicz.sp.ddd.sharedkernel.PositiveAmount;
import pl.mpietrewicz.sp.modules.balance.domain.balance.month.state.Unpaid;

public interface MonthState {

    Amount pay(Month month, PositiveAmount payment);

    Amount refund(Month month, PositiveAmount refund);

    boolean canPaidBy(Month month, Amount payment);

    PaidStatus getPaidStatus();

    Amount getPaid();

    boolean isPaid();

    boolean hasPayment();

    default Amount refund(Month month) {
        Amount refunded = getPaid();
        month.changeState(new Unpaid());
        return refunded;
    }
    
}