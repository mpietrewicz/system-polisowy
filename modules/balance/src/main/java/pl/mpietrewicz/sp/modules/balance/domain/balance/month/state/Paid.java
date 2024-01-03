package pl.mpietrewicz.sp.modules.balance.domain.balance.month.state;

import lombok.NoArgsConstructor;
import pl.mpietrewicz.sp.ddd.sharedkernel.Amount;
import pl.mpietrewicz.sp.ddd.sharedkernel.PositiveAmount;
import pl.mpietrewicz.sp.modules.balance.domain.balance.month.ComponentPremium;
import pl.mpietrewicz.sp.modules.balance.domain.balance.month.Month;
import pl.mpietrewicz.sp.modules.balance.domain.balance.month.MonthState;

import javax.persistence.Entity;
import java.util.List;
import java.util.Optional;

import static pl.mpietrewicz.sp.ddd.sharedkernel.Amount.ZERO;
import static pl.mpietrewicz.sp.modules.balance.domain.balance.month.MonthStatus.PAID;

@Entity
@NoArgsConstructor
public class Paid extends MonthState {

    public Paid(Month month) {
        super(month, PAID, ZERO, ZERO);
    }

    @Override
    public Amount pay(PositiveAmount payment, Optional<Month> nextMonth) {
        if (nextMonth.isEmpty()) {
            month.changeState(new Overpaid(month, payment));
            return ZERO;
        } else {
            return payment;
        }
    }

    @Override
    public Amount refund(PositiveAmount refund, Optional<Month> previousMonth) {
        if (paidIsLessThan(refund)) {
            if (previousMonth.isPresent()) {
                month.changeState(new Unpaid(month));
                return refund.subtract(getPaid());
            } else {
                throw new RuntimeException("You're trying to refund more than you have!");
            }
        } else if (paidEquals(refund)) {
            month.changeState(new Unpaid(month));
        } else if (paidIsHigherThan(refund)) {
            month.changeState(new Underpaid(month, refund));
        }

        return ZERO;
    }

    @Override
    public Month createNextMonth(List<ComponentPremium> componentPremiums) {
        Month previous = this.month;
        return createUnpaid(previous.getYearMonth().plusMonths(1), componentPremiums);
    }

    @Override
    public boolean canPaidBy(Amount payment) {
        return true;
    }

    @Override
    public Amount getPaid() {
        return month.getPremium();
    }

    @Override
    public MonthState getCopy() {
        return new Paid(month);
    }

}