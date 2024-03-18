package pl.mpietrewicz.sp.modules.balance.domain.balance.month.state;

import lombok.NoArgsConstructor;
import pl.mpietrewicz.sp.ddd.annotations.domain.ValueObject;
import pl.mpietrewicz.sp.ddd.sharedkernel.valueobject.Amount;
import pl.mpietrewicz.sp.ddd.sharedkernel.valueobject.PositiveAmount;
import pl.mpietrewicz.sp.modules.balance.domain.balance.month.Month;
import pl.mpietrewicz.sp.modules.balance.domain.balance.month.MonthState;
import pl.mpietrewicz.sp.modules.balance.domain.balance.month.PaidStatus;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

import static pl.mpietrewicz.sp.ddd.sharedkernel.valueobject.Amount.ZERO;

@ValueObject
@Entity
@DiscriminatorValue("UNPAID")
@NoArgsConstructor
public class Unpaid extends MonthState {

    public Unpaid(Month month) {
        super(month, ZERO, PaidStatus.UNPAID);
    }

    @Override
    public Amount pay(PositiveAmount payment) {
        if (payment.isHigherThan(month.getPremium())) {
            month.changeState(new Paid(month, month.getPremium()));
            return payment.subtract(month.getPremium());
        } else if (payment.equals(month.getPremium())) {
            month.changeState(new Paid(month, month.getPremium()));
        } else {
            month.changeState(new Underpaid(month, payment));
        }
        return ZERO;
    }

    @Override
    public Amount refund(PositiveAmount refund) {
        return refund;
    }

    @Override
    public boolean canPaidBy(Amount payment) {
        Amount premium = month.getPremium();
        return payment.isHigherThan(premium) || payment.equals(premium);
    }

    @Override
    public boolean isPaid() {
        return false;
    }

    @Override
    public boolean hasPayment() {
        return false;
    }

    public MonthState createCopy(Month month) {
        return new Unpaid(month);
    }

}