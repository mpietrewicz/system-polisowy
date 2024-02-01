package pl.mpietrewicz.sp.modules.balance.domain.balance.month.state;

import pl.mpietrewicz.sp.ddd.sharedkernel.Amount;
import pl.mpietrewicz.sp.ddd.sharedkernel.PositiveAmount;
import pl.mpietrewicz.sp.modules.balance.domain.balance.month.Month;
import pl.mpietrewicz.sp.modules.balance.domain.balance.month.MonthState;
import pl.mpietrewicz.sp.modules.balance.domain.balance.month.PaidStatus;

import javax.persistence.Embeddable;

import static pl.mpietrewicz.sp.ddd.sharedkernel.Amount.ZERO;

@Embeddable
public class Paid implements MonthState {

    public static final PaidStatus paidStatus = PaidStatus.PAID;

    private Amount paid;

    public Paid() {}

    public Paid(Amount paid) {
        this.paid = paid;
    }

    @Override
    public Amount pay(Month month, PositiveAmount payment) {
        throw new UnsupportedOperationException("Nie można ponownie opłacić opłaconego okresu!");
    }

    @Override
    public Amount refund(Month month, PositiveAmount refund) {
        if (paid.isLessThan(refund)) {
            month.changeState(new Unpaid());
            return refund.subtract(paid);
        } else if (paid.equals(refund)) {
            month.changeState(new Unpaid());
        } else if (paid.isHigherThan(refund)) {
            month.changeState(new Underpaid(paid.subtract(refund)));
        }

        return ZERO;
    }

    @Override
    public boolean canPaidBy(Month month, Amount payment) {
        return true;
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
        return true;
    }

    @Override
    public boolean hasPayment() {
        return true;
    }

}