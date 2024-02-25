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
@DiscriminatorValue("PAID")
@NoArgsConstructor
public class Paid extends MonthState {

    @Enumerated(EnumType.STRING)
    public static final PaidStatus paidStatus = PaidStatus.PAID;

    public Paid(Month month, Amount paid) {
        super(month, paid, paidStatus);
    }

    @Override
    public Amount pay(PositiveAmount payment) {
        throw new UnsupportedOperationException("Nie można ponownie opłacić opłaconego okresu!");
    }

    @Override
    public Amount refund(PositiveAmount refund) {
        if (month.paid.isLessThan(refund)) {
            month.changeState(new Unpaid(month));
            return refund.subtract(month.paid);
        } else if (month.paid.equals(refund)) {
            month.changeState(new Unpaid(month));
        } else if (month.paid.isHigherThan(refund)) {
            month.changeState(new Underpaid(month, month.paid.subtract(refund)));
        }

        return ZERO;
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