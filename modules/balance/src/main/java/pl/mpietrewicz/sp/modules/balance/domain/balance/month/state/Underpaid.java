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
@DiscriminatorValue("UNDERPAID")
@NoArgsConstructor
public class Underpaid extends MonthState {

    public Underpaid(Month month, Amount paid) {
        super(month, paid, PaidStatus.UNDERPAID);
    }

    @Override
    public Amount pay(PositiveAmount payment) {
        Amount underpayment = month.getPremium().subtract(month.getPaid());

        if (payment.isHigherThan(underpayment)) {
            month.changeState(new Paid(month, month.getPremium()));
            return payment.subtract(underpayment);
        } else if (payment.equals(underpayment)) {
            month.changeState(new Paid(month, month.getPremium()));
        } else {
            month.changeState(new Underpaid(month, month.getPaid().add(payment)));
        }
        return ZERO;
    }

    @Override
    public Amount refund(PositiveAmount refund) {
        Amount paid = month.getPaid();

        if (refund.isHigherThan(paid)) {
            month.changeState(new Unpaid(month));
            return refund.subtract(paid);
        } else if (refund.equals(paid)) {
            month.changeState(new Unpaid(month));
        } else {
            month.changeState(new Underpaid(month, paid.subtract(refund)));
        }
        return ZERO;
    }

    @Override
    public boolean canPaidBy(Amount payment) {
        Amount underpayment = month.getPremium().subtract(month.getPaid());
        return payment.isHigherThan(underpayment) || payment.equals(underpayment);
    }

    @Override
    public boolean isPaid() {
        return false;
    }

    @Override
    public boolean hasPayment() {
        return true;
    }

    public MonthState createCopy(Month month) {
        return new Underpaid(month, getPaid());
    }

}