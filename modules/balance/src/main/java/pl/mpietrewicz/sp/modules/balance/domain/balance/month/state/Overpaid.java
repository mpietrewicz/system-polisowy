package pl.mpietrewicz.sp.modules.balance.domain.balance.month.state;

import lombok.NoArgsConstructor;
import pl.mpietrewicz.sp.ddd.sharedkernel.Amount;
import pl.mpietrewicz.sp.ddd.sharedkernel.PositiveAmount;
import pl.mpietrewicz.sp.modules.balance.domain.balance.month.Month;
import pl.mpietrewicz.sp.modules.balance.domain.balance.month.MonthState;
import pl.mpietrewicz.sp.modules.balance.domain.balance.month.MonthStatus;

import javax.persistence.Entity;

import static pl.mpietrewicz.sp.ddd.sharedkernel.Amount.ZERO;

@Entity
@NoArgsConstructor
public class Overpaid extends MonthState {

    public Overpaid(Month month, Amount paid) {
        super(month, MonthStatus.OVERPAID, paid);
    }

    public Amount pay(PositiveAmount payment) {
        paid = paid.add(payment);
        return ZERO;
    }

    public Amount refund(PositiveAmount refund) {
        if (refund.isHigherThan(paid)) {
            month.changeState(new Unpaid(month));
            return refund.subtract(paid);
        } else if (refund.equals(paid)) {
            month.changeState(new Unpaid(month));
            return ZERO;
        } else {
            Amount overpayment = paid.subtract(getPremium());
            if (overpayment.isHigherThan(refund)) {
                paid = paid.subtract(refund);
            } else if (refund.equals(overpayment)) {
                month.changeState(new Paid(month));
            } else {
                month.changeState(new Underpaid(month, paid.subtract(refund)));
            }
            return ZERO;
        }
    }

    @Override
    public boolean canPaidBy(Amount payment) {
        return true;
    }

}