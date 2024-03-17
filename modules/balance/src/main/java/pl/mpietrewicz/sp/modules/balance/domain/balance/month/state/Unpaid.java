package pl.mpietrewicz.sp.modules.balance.domain.balance.month.state;

import pl.mpietrewicz.sp.ddd.annotations.domain.ValueObject;
import pl.mpietrewicz.sp.ddd.sharedkernel.Amount;
import pl.mpietrewicz.sp.ddd.sharedkernel.PositiveAmount;
import pl.mpietrewicz.sp.modules.balance.domain.balance.month.Month;
import pl.mpietrewicz.sp.modules.balance.domain.balance.month.MonthState;
import pl.mpietrewicz.sp.modules.balance.domain.balance.month.PaidStatus;
import pl.mpietrewicz.sp.modules.balance.exceptions.RefundException;

import static pl.mpietrewicz.sp.ddd.sharedkernel.Amount.ZERO;

@ValueObject
public class Unpaid extends MonthState {

    private static final PaidStatus paidStatus = PaidStatus.UNPAID;

    public Unpaid(Month month) {
        super(month, ZERO, paidStatus);
    }

    @Override
    public Amount pay(PositiveAmount payment) {
        if (payment.isHigherThan(month.getPremium())) {
            month.changeState(new Paid(month, month.getPremium()));
            return payment.subtract(month.getPremium());
        } else if (payment.equals(month.getPremium())) {
            month.changeState(new Paid(month, month.getPremium()));
        } else {
            month.changeState(new Underpaid(month, payment));
        }
        return ZERO;
    }

    @Override
    public Amount refund(PositiveAmount refund) throws RefundException {
        throw new RefundException("You trying refund on the unpaid period!");
    }

    @Override
    public boolean canPaidBy(Amount payment) {
        Amount premium = month.getPremium();
        return payment.isHigherThan(premium) || payment.equals(premium);
    }

    @Override
    public PaidStatus getPaidStatus() {
        return paidStatus;
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