package pl.mpietrewicz.sp.modules.balance.domain.balance.month.state;

import pl.mpietrewicz.sp.ddd.sharedkernel.Amount;
import pl.mpietrewicz.sp.ddd.sharedkernel.PositiveAmount;
import pl.mpietrewicz.sp.modules.balance.domain.balance.month.Month;
import pl.mpietrewicz.sp.modules.balance.domain.balance.month.MonthState;
import pl.mpietrewicz.sp.modules.balance.domain.balance.month.PaidStatus;

import javax.persistence.Embeddable;

import static pl.mpietrewicz.sp.ddd.sharedkernel.Amount.ZERO;

@Embeddable
public class Underpaid implements MonthState {

    private static final PaidStatus paidStatus = PaidStatus.UNDERPAID;

    private Amount paid;

    public Underpaid() {}

    public Underpaid(Amount paid) {
        this.paid = paid;
    }

    @Override
    public Amount pay(Month month, PositiveAmount payment) {
        Amount premium = month.getPremium();
        Amount underpayment = premium.subtract(paid);

        if (payment.isHigherThan(underpayment)) {
            month.changeState(new Paid(premium));
            return payment.subtract(underpayment);
        } else if (payment.equals(underpayment)) {
            month.changeState(new Paid(premium));
        } else {
            month.changeState(new Underpaid(paid.add(payment)));
        }
        return ZERO;
    }

    @Override
    public Amount refund(Month month, PositiveAmount refund) {
        if (refund.isHigherThan(paid)) {
            month.changeState(new Unpaid());
            return refund.subtract(paid);
        } else if (refund.equals(paid)) {
            month.changeState(new Unpaid());
        } else {
            month.changeState(new Underpaid(paid.subtract(refund)));
        }
        return ZERO;
    }

    @Override
    public boolean canPaidBy(Month month, Amount payment) {
        Amount underpayment = month.getPremium().subtract(paid);
        return payment.isHigherThan(underpayment) || payment.equals(underpayment);
    }

    @Override
    public PaidStatus getPaidStatus() {
        return paidStatus;
    }

    @Override
    public Amount getPaid() {
        return paid;
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