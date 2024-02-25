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
@DiscriminatorValue("OVERPAID")
@NoArgsConstructor
public class Overpaid extends MonthState {

    public static final PaidStatus paidStatus = PaidStatus.OVERPAID;

    public Overpaid(Month month, Amount paid) {
        super(month, paid, paidStatus);
    }

    public Amount pay(PositiveAmount payment) {
        month.changeState(new Overpaid(month, month.paid.add(payment)));
        return ZERO;
    }

    public Amount refund(PositiveAmount refund) {
        if (refund.isHigherThan(month.paid)) {
            month.changeState(new Unpaid(month));
            return refund.subtract(month.paid);
        } else if (refund.equals(month.paid)) {
            month.changeState(new Unpaid(month));
            return ZERO;
        } else {
            Amount premium = month.premium;
            Amount overpayment = month.paid.subtract(premium);
            if (overpayment.isHigherThan(refund)) {
                month.changeState(new Overpaid(month, month.paid.subtract(refund)));
            } else if (refund.equals(overpayment)) {
                month.changeState(new Paid(month, month.premium));
            } else {
                month.changeState(new Underpaid(month, month.paid.subtract(refund)));
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