package pl.mpietrewicz.sp.modules.balance.domain.balance.month.state;

import lombok.NoArgsConstructor;
import pl.mpietrewicz.sp.ddd.sharedkernel.Amount;
import pl.mpietrewicz.sp.ddd.sharedkernel.PositiveAmount;
import pl.mpietrewicz.sp.modules.balance.domain.balance.month.ComponentPremium;
import pl.mpietrewicz.sp.modules.balance.domain.balance.month.Month;
import pl.mpietrewicz.sp.modules.balance.domain.balance.month.MonthState;
import pl.mpietrewicz.sp.modules.balance.domain.balance.month.MonthStatus;

import javax.persistence.Entity;
import java.util.List;
import java.util.Optional;

import static pl.mpietrewicz.sp.ddd.sharedkernel.Amount.ZERO;

@Entity
@NoArgsConstructor
public class Unpaid extends MonthState {

    public Unpaid(Month month) {
        super(month, MonthStatus.UNPAID, ZERO, ZERO);
    }

    @Override
    public Amount pay(PositiveAmount payment, Optional<Month> nextMonth) {
        if (premiumIsLessThan(payment)) {
            if (nextMonth.isPresent()) {
                month.changeState(new Paid(month));
                return payment.subtract(getPremium());
            } else {
                Amount overpayment = payment.subtract(getPremium());
                month.changeState(new Overpaid(month, overpayment));
            }
        } else if (premiumEquals(payment)) {
            month.changeState(new Paid(month));
        } else if (premiumIsHigherThan(payment)) {
            Amount underpayment = getPremium().subtract(payment);
            month.changeState(new Underpaid(month, underpayment));
        }

        return ZERO;
    }

    @Override
    public Amount refund(PositiveAmount refund, Optional<Month> previousMonth) {
        if (previousMonth.isPresent()) {
            return refund;
        } else {
            throw new IllegalStateException("Nie można zwrócić więcej niż sauma kwot");
        }
    }

    @Override
    public boolean canPaidBy(Amount payment) {
        return premiumIsLessThan(payment) || premiumEquals(payment);
    }

    @Override
    public Month createNextMonth(List<ComponentPremium> componentPremiums) {
        Month previous = this.month;
        return createUnpaid(previous.getYearMonth().plusMonths(1), componentPremiums);
    }

    @Override
    public Amount getPaid() {
        return ZERO;
    }

    @Override
    public MonthState getCopy() {
        return new Underpaid(month, underpayment);
    }

    private boolean premiumIsLessThan(Amount amount) {
        return month.getPremium().isLessThan(amount);
    }

    private boolean premiumEquals(Amount amount) {
        return month.getPremium().equals(amount);
    }

    private boolean premiumIsHigherThan(Amount amount) {
        return month.getPremium().isHigherThan(amount);
    }

}