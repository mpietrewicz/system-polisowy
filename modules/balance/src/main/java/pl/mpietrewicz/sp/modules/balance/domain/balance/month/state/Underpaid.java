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
import static pl.mpietrewicz.sp.modules.balance.domain.balance.month.MonthStatus.UNDERPAID;

@Entity
@NoArgsConstructor
public class Underpaid extends MonthState {

    public Underpaid(Month month, Amount underpayment) {
        super(month, UNDERPAID, underpayment, ZERO);
    }

    @Override
    public Amount pay(PositiveAmount payment, Optional<Month> nextMonth) {
        if (underpaymentIsLessThan(payment)) {
            if (nextMonth.isPresent()) {
                month.changeState(new Paid(month));
                return payment.subtract(underpayment);
            } else {
                Amount overpayment = payment.subtract(underpayment);
                month.changeState(new Overpaid(month, overpayment));
            }
        } else if (underpaymentEquals(payment)) {
            month.changeState(new Paid(month));
        } else if (underpaymentIsHigherThan(payment)) {
            decreaseUnderpayment(payment);
        }

        return ZERO;
    }

    @Override
    public Amount refund(PositiveAmount refund, Optional<Month> previousMonth) {
        if (paidIsLessThan(refund)) {
            if (previousMonth.isPresent()) {
                month.changeState(new Unpaid(month));
                return refund.subtract(getPaid());
            } else {
                throw new IllegalStateException("You're trying to refund more than you have!");
            }
        } else if (paidEquals(refund)) {
            month.changeState(new Unpaid(month));
        } else if (paidIsHigherThan(refund)) {
            increaseUnderpayment(refund);
        }

        return ZERO;
    }

    @Override
    public boolean canPaidBy(Amount payment) {
        return underpaymentIsLessThan(payment) || underpaymentEquals(payment);
    }

    @Override
    public Month createNextMonth(List<ComponentPremium> componentPremiums) {
        Month previous = this.month;
        return createUnpaid(previous.getYearMonth().plusMonths(1), componentPremiums);
    }

    @Override
    public Amount getPaid() {
        return month.getPremium().subtract(underpayment);
    }

    @Override
    public MonthState getCopy() {
        return new Underpaid(month, underpayment);
    }

    private void increaseUnderpayment(Amount amount) {
        this.underpayment = this.underpayment.add(amount);
    }

    private void decreaseUnderpayment(Amount amount) {
        this.underpayment = this.underpayment.subtract(amount);
    }

    private boolean underpaymentIsLessThan(Amount amount) {
        return this.underpayment.isLessThan(amount);
    }

    private boolean underpaymentEquals(Amount amount) {
        return this.underpayment.equals(amount);
    }

    private boolean underpaymentIsHigherThan(Amount amount) {
        return this.underpayment.isHigherThan(amount);
    }

}