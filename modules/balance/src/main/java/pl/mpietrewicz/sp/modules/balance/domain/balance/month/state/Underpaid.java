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
public class Underpaid extends MonthState {

    public Underpaid(Month month, Amount paid) {
        super(month, MonthStatus.UNDERPAID, paid);
    }

    @Override
    public Amount pay(PositiveAmount payment) {
        Amount underpayment = getPremium().subtract(paid);

        if (payment.isHigherThan(underpayment)) {
            month.changeState(new Paid(month));
            return payment.subtract(underpayment);
        } else if (payment.equals(underpayment)) {
            month.changeState(new Paid(month));
            return ZERO;
        } else {
            paid = paid.add(payment);
            return ZERO;
        }
    }

    @Override
    public Amount refund(PositiveAmount refund) {
        if (refund.isHigherThan(paid)) {
            month.changeState(new Unpaid(month));
            return refund.subtract(paid);
        } else if (refund.equals(paid)) {
            month.changeState(new Unpaid(month));
            return ZERO;
        } else {
            paid = paid.subtract(refund);
            return ZERO;
        }
    }

    @Override
    public boolean canPaidBy(Amount payment) {
        Amount underpayment = getPremium().subtract(paid);
        return payment.isHigherThan(underpayment) || payment.equals(underpayment);
    }

}