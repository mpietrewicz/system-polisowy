package pl.mpietrewicz.sp.modules.balance.domain.balance.month.state;

import pl.mpietrewicz.sp.ddd.sharedkernel.Amount;
import pl.mpietrewicz.sp.ddd.sharedkernel.PositiveAmount;
import pl.mpietrewicz.sp.modules.balance.domain.balance.month.Month;
import pl.mpietrewicz.sp.modules.balance.domain.balance.month.MonthState;
import pl.mpietrewicz.sp.modules.balance.domain.balance.month.PaidStatus;

import javax.persistence.Embeddable;

import static pl.mpietrewicz.sp.ddd.sharedkernel.Amount.ZERO;

@Embeddable
public class Unpaid implements MonthState {

    private static final PaidStatus paidStatus = PaidStatus.UNPAID;

    @Override
    public Amount pay(Month month, PositiveAmount payment) {
        Amount premium = month.getPremium();

        if (payment.isHigherThan(premium)) {
            month.changeState(new Paid(premium));
            return payment.subtract(premium);
        } else if (payment.equals(premium)) {
            month.changeState(new Paid(premium));
        } else {
            month.changeState(new Underpaid(payment));
        }
        return ZERO;
    }

    @Override
    public Amount refund(Month month, PositiveAmount refund) {
        throw new UnsupportedOperationException("Nie można zwrócic środków na nieopłaconym okresie!");
    }

    @Override
    public boolean canPaidBy(Month month, Amount payment) {
        Amount premium = month.getPremium();
        return payment.isHigherThan(premium) || payment.equals(premium);
    }

    @Override
    public PaidStatus getPaidStatus() {
        return paidStatus;
    }

    @Override
    public Amount getPaid() {
        return ZERO;
    }

    @Override
    public boolean isPaid() {
        return false;
    }

    @Override
    public boolean hasPayment() {
        return false;
    }

}