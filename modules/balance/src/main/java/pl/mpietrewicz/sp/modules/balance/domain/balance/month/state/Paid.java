package pl.mpietrewicz.sp.modules.balance.domain.balance.month.state;

import lombok.NoArgsConstructor;
import pl.mpietrewicz.sp.ddd.annotations.domain.ValueObject;
import pl.mpietrewicz.sp.ddd.sharedkernel.valueobject.Amount;
import pl.mpietrewicz.sp.ddd.sharedkernel.valueobject.PositiveAmount;
import pl.mpietrewicz.sp.ddd.sharedkernel.valueobject.ZeroAmount;
import pl.mpietrewicz.sp.modules.balance.domain.balance.month.Month;
import pl.mpietrewicz.sp.modules.balance.domain.balance.month.MonthState;
import pl.mpietrewicz.sp.modules.balance.domain.balance.month.PaidStatus;
import pl.mpietrewicz.sp.modules.balance.exceptions.NoMonthsToRefundException;

import javax.persistence.AttributeOverride;
import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Embedded;
import javax.persistence.Entity;

@ValueObject
@Entity
@DiscriminatorValue("PAID")
@NoArgsConstructor
public class Paid extends MonthState {

    @Embedded
    @AttributeOverride(name = "value", column = @Column(name = "paid", nullable = false))
    private PositiveAmount paid;

    public Paid(Month month, PositiveAmount paid) {
        super(month, PaidStatus.PAID);
        this.paid = paid;
    }

    @Override
    public Amount pay(PositiveAmount payment) {
        return payment;
    }

    @Override
    public Amount refund(PositiveAmount refund) throws NoMonthsToRefundException {
        if (paid.isLessThan(refund)) {
            month.changeState(new Unpaid(month));
            return refund.subtract(paid);
        } else if (paid.equals(refund)) {
            month.changeState(new Unpaid(month));
        } else if (paid.isHigherThan(refund)) {
            month.changeState(new Underpaid(month, paid.subtract(refund)));
        }
        return new ZeroAmount();
    }

    @Override
    public Amount refund() {
        PositiveAmount paidBeforeRefund = paid;
        month.changeState(new Unpaid(month));
        return paidBeforeRefund;
    }

    @Override
    public boolean canPaidBy(PositiveAmount payment) {
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
        return new Paid(month, paid);
    }

    @Override
    public Amount getPaid() {
        return paid;
    }


}