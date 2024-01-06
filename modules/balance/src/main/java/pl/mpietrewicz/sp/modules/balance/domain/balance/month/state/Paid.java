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
public class Paid extends MonthState {

    public Paid(Month month) {
        super(month, MonthStatus.PAID);
    }

    @Override
    public Amount pay(PositiveAmount payment) {
        throw new UnsupportedOperationException("Nie można ponownie opłacić opłaconego okresu!");
    }

    @Override
    public Amount refund(PositiveAmount refund) {
        if (paid.isLessThan(refund)) {
            month.changeState(new Unpaid(month));
            return refund.subtract(paid);
        } else if (paid.equals(refund)) {
            month.changeState(new Unpaid(month));
        } else if (paid.isHigherThan(refund)) {
            month.changeState(new Underpaid(month, paid.subtract(refund)));
        }

        return ZERO;
    }

    @Override
    public boolean canPaidBy(Amount payment) {
        return true;
    }

}