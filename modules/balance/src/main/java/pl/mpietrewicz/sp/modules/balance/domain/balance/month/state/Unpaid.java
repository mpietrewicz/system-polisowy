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
@DiscriminatorValue("UNPAID")
@NoArgsConstructor
public class Unpaid extends MonthState {

    @Enumerated(EnumType.STRING)
    private static final PaidStatus paidStatus = PaidStatus.UNPAID;

    public Unpaid(Month month) {
        super(month, ZERO, paidStatus);
    }

    @Override
    public Amount pay(PositiveAmount payment) {
        if (payment.isHigherThan(month.premium)) {
            month.changeState(new Paid(month, month.premium));
            return payment.subtract(month.premium);
        } else if (payment.equals(month.premium)) {
            month.changeState(new Paid(month, month.premium));
        } else {
            month.changeState(new Underpaid(month, payment));
        }
        return ZERO;
    }

    @Override
    public Amount refund(PositiveAmount refund) {
        throw new UnsupportedOperationException("Nie można zwrócic środków na nieopłaconym okresie!");
    }

    @Override
    public boolean canPaidBy(Amount payment) {
        Amount premium = month.premium;
        return payment.isHigherThan(premium) || payment.equals(premium);
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
        return false;
    }

}