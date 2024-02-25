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
import javax.persistence.EnumType;
import javax.persistence.Enumerated;

import static pl.mpietrewicz.sp.ddd.sharedkernel.Amount.ZERO;

@ValueObject
@Entity
@DiscriminatorValue("UNDERPAID")
@NoArgsConstructor
public class Underpaid extends MonthState {

    @Enumerated(EnumType.STRING)
    private static final PaidStatus paidStatus = PaidStatus.UNDERPAID;

    public Underpaid(Month month, Amount paid) {
        super(month, paid, paidStatus);
    }

    @Override
    public Amount pay(PositiveAmount payment) {
        Amount underpayment = month.premium.subtract(month.paid);

        if (payment.isHigherThan(underpayment)) {
            month.changeState(new Paid(month, month.premium));
            return payment.subtract(underpayment);
        } else if (payment.equals(underpayment)) {
            month.changeState(new Paid(month, month.premium));
        } else {
            month.changeState(new Underpaid(month, month.paid.add(payment)));
        }
        return ZERO;
    }

    @Override
    public Amount refund(PositiveAmount refund) {
        if (refund.isHigherThan(month.paid)) {
            month.changeState(new Unpaid(month));
            return refund.subtract(month.paid);
        } else if (refund.equals(month.paid)) {
            month.changeState(new Unpaid(month));
        } else {
            month.changeState(new Underpaid(month, month.paid.subtract(refund)));
        }
        return ZERO;
    }

    @Override
    public boolean canPaidBy(Amount payment) {
        Amount underpayment = month.premium.subtract(month.paid);
        return payment.isHigherThan(underpayment) || payment.equals(underpayment);
    }

    @Override
    public PaidStatus getPaidStatus() {
        return paidStatus;
    }

    @Override
    public boolean isPaid() {
        return false;
    }

    @Override
    public boolean hasPayment() {
        return true;
    }

}