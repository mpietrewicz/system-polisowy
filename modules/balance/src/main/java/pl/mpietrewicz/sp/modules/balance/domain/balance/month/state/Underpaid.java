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
public class Underpaid extends MonthState {

    private static final PaidStatus paidStatus = PaidStatus.UNDERPAID;

    public Underpaid(Month month, Amount paid) {
        super(month, paid, paidStatus);
    }

    @Override
    public Amount pay(PositiveAmount payment) throws PaymentException {
        Amount underpayment = month.getPremium().subtract(month.getPaid());

        if (payment.isHigherThan(underpayment)) {
            month.changeState(new Paid(month, month.getPremium()));
            return payment.subtract(underpayment);
        } else if (payment.equals(underpayment)) {
            month.changeState(new Paid(month, month.getPremium()));
        } else {
            month.changeState(new Underpaid(month, month.getPaid().add(payment)));
        }
        return ZERO;
    }

    @Override
    public Amount refund(PositiveAmount refund) throws RefundException {
        if (refund.isHigherThan(month.getPaid())) {
            month.changeState(new Unpaid(month));
            return refund.subtract(month.getPaid());
        } else if (refund.equals(month.getPaid())) {
            month.changeState(new Unpaid(month));
        } else {
            month.changeState(new Underpaid(month, month.getPaid().subtract(refund)));
        }
        return ZERO;
    }

    @Override
    public boolean canPaidBy(Amount payment) {
        Amount underpayment = month.getPremium().subtract(month.getPaid());
        return payment.isHigherThan(underpayment) || payment.equals(underpayment);
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
        return true;
    }

}