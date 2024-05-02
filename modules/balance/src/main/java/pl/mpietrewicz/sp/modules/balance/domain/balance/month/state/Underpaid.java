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
@DiscriminatorValue("UNDERPAID")
@NoArgsConstructor
public class Underpaid extends MonthState {

    @Embedded
    @AttributeOverride(name = "value", column = @Column(name = "paid", nullable = false))
    private PositiveAmount paid;

    public Underpaid(Month month, PositiveAmount paid) {
        super(month, PaidStatus.UNDERPAID);
        this.paid = paid;
    }

    @Override
    public Amount pay(PositiveAmount payment) {
        PositiveAmount underpayment = month.getPremium().subtract(paid);

        if (payment.isHigherThan(underpayment)) {
            month.changeState(new Paid(month, month.getPremium()));
            return payment.subtract(underpayment);
        } else if (payment.equals(underpayment)) {
            month.changeState(new Paid(month, month.getPremium()));
        } else {
            month.changeState(new Underpaid(month, paid.add(payment)));
        }
        return new ZeroAmount();
    }

    @Override
    public Amount refund(PositiveAmount refund) throws NoMonthsToRefundException {
        if (refund.isHigherThan(paid)) {
            month.changeState(new Unpaid(month));
            return refund.subtract(paid);
        } else if (refund.equals(paid)) {
            month.changeState(new Unpaid(month));
        } else {
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
        PositiveAmount underpayment = month.getPremium().subtract(paid);
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
        return new Underpaid(month, paid);
    }

    @Override
    public Amount getPaid() {
        return paid;
    }

}