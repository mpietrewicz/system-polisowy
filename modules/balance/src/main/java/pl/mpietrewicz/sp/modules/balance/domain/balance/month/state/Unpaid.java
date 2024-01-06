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
public class Unpaid extends MonthState {

    public Unpaid(Month month) {
        super(month, MonthStatus.UNPAID);
    }

    @Override
    public Amount pay(PositiveAmount payment) {
        Amount premium = getPremium();

        if (payment.isHigherThan(premium)) {
            month.changeState(new Paid(month));
            return payment.subtract(premium);
        } else if (payment.equals(premium)) {
            month.changeState(new Paid(month));
            return ZERO;
        } else {
            month.changeState(new Underpaid(month, payment));
            return ZERO;
        }
    }

    @Override
    public Amount refund(PositiveAmount refund) {
        throw new UnsupportedOperationException("Nie można zwrócic środków na nieopłaconym okresie!");
    }

    @Override
    public boolean canPaidBy(Amount payment) {
        Amount premium = getPremium();
        return payment.isHigherThan(premium) || payment.equals(premium);
    }

}