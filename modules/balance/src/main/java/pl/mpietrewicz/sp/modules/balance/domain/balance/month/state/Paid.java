package pl.mpietrewicz.sp.modules.balance.domain.balance.month.state;

import lombok.NoArgsConstructor;
import pl.mpietrewicz.sp.ddd.annotations.domain.ValueObject;
import pl.mpietrewicz.sp.ddd.sharedkernel.Amount;
import pl.mpietrewicz.sp.ddd.sharedkernel.PositiveAmount;
import pl.mpietrewicz.sp.modules.balance.domain.balance.month.Month;
import pl.mpietrewicz.sp.modules.balance.domain.balance.month.MonthState;
import pl.mpietrewicz.sp.modules.balance.domain.balance.month.PaidStatus;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

import static pl.mpietrewicz.sp.ddd.sharedkernel.Amount.ZERO;

@ValueObject
@Entity
@DiscriminatorValue("PAID")
@NoArgsConstructor
public class Paid extends MonthState {

    public Paid(Month month, Amount paid) {
        super(month, paid, PaidStatus.PAID);
    }

    @Override
    public Amount pay(PositiveAmount payment) {
        return payment;
    }

    @Override
    public Amount refund(PositiveAmount refund) {
        Amount paid = month.getPaid();

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

    @Override
    public boolean isPaid() {
        return true;
    }

    @Override
    public boolean hasPayment() {
        return true;
    }

    public MonthState createCopy(Month month) {
        return new Paid(month, getPaid());
    }


}