package pl.mpietrewicz.sp.modules.balance.domain.balance.month.state;

import pl.mpietrewicz.sp.ddd.annotations.domain.ValueObject;
import pl.mpietrewicz.sp.ddd.sharedkernel.Amount;
import pl.mpietrewicz.sp.ddd.sharedkernel.PositiveAmount;
import pl.mpietrewicz.sp.modules.balance.domain.balance.month.Month;
import pl.mpietrewicz.sp.modules.balance.domain.balance.month.MonthState;
import pl.mpietrewicz.sp.modules.balance.domain.balance.month.PaidStatus;
import pl.mpietrewicz.sp.modules.balance.exceptions.PaymentException;
import pl.mpietrewicz.sp.modules.balance.exceptions.RefundException;

import static pl.mpietrewicz.sp.ddd.sharedkernel.Amount.ZERO;

@ValueObject
public class Paid extends MonthState {

    public static final PaidStatus paidStatus = PaidStatus.PAID;

    public Paid(Month month, Amount paid) {
        super(month, paid, paidStatus);
    }

    @Override
    public Amount pay(PositiveAmount payment) throws PaymentException {
        throw new PaymentException("You trying pay the paid period!");
    }

    @Override
    public Amount refund(PositiveAmount refund) throws RefundException {
        if (month.getPaid().isLessThan(refund)) {
            month.changeState(new Unpaid(month));
            return refund.subtract(month.getPaid());
        } else if (month.getPaid().equals(refund)) {
            month.changeState(new Unpaid(month));
        } else if (month.getPaid().isHigherThan(refund)) {
            month.changeState(new Underpaid(month, month.getPaid().subtract(refund)));
        }
        return ZERO;
    }

    @Override
    public boolean canPaidBy(Amount payment) {
        return true;
    }

    @Override
    public PaidStatus getPaidStatus() {
        return paidStatus;
    }

    @Override
    public boolean isPaid() {
        return true;
    }

    @Override
    public boolean hasPayment() {
        return true;
    }


}