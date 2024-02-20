package pl.mpietrewicz.sp.modules.balance.domain.balance.month.state;

import pl.mpietrewicz.sp.ddd.annotations.domain.ValueObject;
import pl.mpietrewicz.sp.ddd.sharedkernel.Amount;
import pl.mpietrewicz.sp.ddd.sharedkernel.PositiveAmount;
import pl.mpietrewicz.sp.modules.balance.domain.balance.month.Month;
import pl.mpietrewicz.sp.modules.balance.domain.balance.month.MonthState;
import pl.mpietrewicz.sp.modules.balance.domain.balance.month.PaidStatus;

import static pl.mpietrewicz.sp.ddd.sharedkernel.Amount.ZERO;

@ValueObject
public class Overpaid extends MonthState {

    public static final PaidStatus paidStatus = PaidStatus.OVERPAID;

    public Overpaid(Month month, Amount paid) {
        super(month, paid, paidStatus);
    }

    public Amount pay(PositiveAmount payment) {
        month.changeState(new Overpaid(month, month.getPaid().add(payment)));
        return ZERO;
    }

    public Amount refund(PositiveAmount refund) {
        if (refund.isHigherThan(month.getPaid())) {
            month.changeState(new Unpaid(month));
            return refund.subtract(month.getPaid());
        } else if (refund.equals(month.getPaid())) {
            month.changeState(new Unpaid(month));
            return ZERO;
        } else {
            Amount premium = month.getPremium();
            Amount overpayment = month.getPaid().subtract(premium);
            if (overpayment.isHigherThan(refund)) {
                month.changeState(new Overpaid(month, month.getPaid().subtract(refund)));
            } else if (refund.equals(overpayment)) {
                month.changeState(new Paid(month, month.getPremium()));
            } else {
                month.changeState(new Underpaid(month, month.getPaid().subtract(refund)));
            }
            return ZERO;
        }
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