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
@DiscriminatorValue("UNPAID")
@NoArgsConstructor
public class Unpaid extends MonthState {

    @Embedded
    @AttributeOverride(name = "value", column = @Column(name = "paid", nullable = false))
    private final ZeroAmount zero = new ZeroAmount();

    public Unpaid(Month month) {
        super(month, PaidStatus.UNPAID);
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
        return new ZeroAmount();
    }

    @Override
    public Amount refund(PositiveAmount refund) throws NoMonthsToRefundException {
        throw new NoMonthsToRefundException("Trying refund unpaid month!");
    }

    @Override
    public Amount refund() {
        return new ZeroAmount();
    }

    @Override
    public boolean canPaidBy(PositiveAmount payment) {
        PositiveAmount premium = month.getPremium();
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

    @Override
    public Amount getPaid() {
        return zero;
    }

}