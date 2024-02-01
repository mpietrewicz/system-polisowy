package pl.mpietrewicz.sp.modules.balance.domain.balance.month.state;

import pl.mpietrewicz.sp.ddd.sharedkernel.Amount;
import pl.mpietrewicz.sp.ddd.sharedkernel.PositiveAmount;
import pl.mpietrewicz.sp.modules.balance.domain.balance.month.Month;
import pl.mpietrewicz.sp.modules.balance.domain.balance.month.MonthState;
import pl.mpietrewicz.sp.modules.balance.domain.balance.month.PaidStatus;

import javax.persistence.Embeddable;

import static pl.mpietrewicz.sp.ddd.sharedkernel.Amount.ZERO;

@Embeddable
public class Overpaid implements MonthState {

    public static final PaidStatus paidStatus = PaidStatus.OVERPAID;

    private Amount paid;

    public Overpaid() {}

    public Overpaid(Amount paid) {
        this.paid = paid;
    }

    public Amount pay(Month month, PositiveAmount payment) {
        paid = paid.add(payment);
        month.changeState(new Overpaid(paid));
        return ZERO;
    }

    public Amount refund(Month month, PositiveAmount refund) {
        if (refund.isHigherThan(paid)) {
            month.changeState(new Unpaid());
            return refund.subtract(paid);
        } else if (refund.equals(paid)) {
            month.changeState(new Unpaid());
            return ZERO;
        } else {
            Amount premium = month.getPremium();
            Amount overpayment = paid.subtract(premium);
            if (overpayment.isHigherThan(refund)) {
                month.changeState(new Overpaid(paid.subtract(refund)));
            } else if (refund.equals(overpayment)) {
                month.changeState(new Paid(premium));
            } else {
                month.changeState(new Underpaid(paid.subtract(refund)));
            }
            return ZERO;
        }
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