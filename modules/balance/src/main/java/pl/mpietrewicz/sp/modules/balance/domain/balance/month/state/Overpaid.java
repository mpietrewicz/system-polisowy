package pl.mpietrewicz.sp.modules.balance.domain.balance.month.state;

import lombok.NoArgsConstructor;
import pl.mpietrewicz.sp.ddd.sharedkernel.Amount;
import pl.mpietrewicz.sp.ddd.sharedkernel.PositiveAmount;
import pl.mpietrewicz.sp.modules.balance.domain.balance.month.ComponentPremium;
import pl.mpietrewicz.sp.modules.balance.domain.balance.month.Month;
import pl.mpietrewicz.sp.modules.balance.domain.balance.month.MonthState;

import javax.persistence.Entity;
import java.time.YearMonth;
import java.util.List;
import java.util.Optional;

import static pl.mpietrewicz.sp.ddd.sharedkernel.Amount.ZERO;
import static pl.mpietrewicz.sp.modules.balance.domain.balance.month.MonthStatus.OVERPAID;

@Entity
@NoArgsConstructor
public class Overpaid extends MonthState {

    public Overpaid(Month month, Amount overpayment) {
        super(month, OVERPAID, ZERO, overpayment);
    }

    public Amount pay(PositiveAmount payment, Optional<Month> nextMonth) {
        if (nextMonth.isPresent()) {
            throw new RuntimeException("The overpaid month is not last!");
        } else {
            increaseOverpayment(payment);
            return ZERO;
        }
    }

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
            if (overpaymentIsLessThan(refund)) {
                month.changeState(new Underpaid(month, refund.subtract(overpayment)));
            } else if (overpaymentEquals(refund)) {
                month.changeState(new Paid(month));
            } else if (overpaymentIsHigherThan(refund)) {
                decreaseOverpayment(refund);
            }
        }

        return ZERO;
    }

    @Override
    public boolean canPaidBy(Amount payment) {
        return true;
    }

    @Override
    public Month createNextMonth(List<ComponentPremium> newComponentPremiums) {
        Amount premium = newComponentPremiums.stream()
                .map(ComponentPremium::getPremium)
                .reduce(ZERO, Amount::add);

        Month previous = this.month;
        previous.changeState(new Paid(month));
        YearMonth nextYearMonth = previous.getYearMonth().plusMonths(1);

        if (overpaymentIsLessThan(premium)) {
            Amount underpayment = premium.subtract(overpayment);
            return createUnderpaid(nextYearMonth, underpayment, newComponentPremiums);
        } else if (overpaymentEquals(premium)) {
            return createPaid(nextYearMonth, newComponentPremiums);
        } else if (overpaymentIsHigherThan(premium)) {
            Amount newOverpayment = overpayment.subtract(premium);
            return createOverpaid(nextYearMonth, newOverpayment, newComponentPremiums);
        } else {
            throw new IllegalStateException();
        }
    }

    @Override
    public Amount getPaid() {
        return getPremium().add(overpayment);
    }

    @Override
    public MonthState getCopy() {
        return new Overpaid(month, overpayment);
    }

    private void increaseOverpayment(Amount amount) {
        this.overpayment = this.overpayment.add(amount);
    }

    private void decreaseOverpayment(Amount amount) {
        this.overpayment = this.overpayment.subtract(amount);
    }

    private boolean overpaymentIsLessThan(Amount amount) {
        return this.overpayment.isLessThan(amount);
    }

    private boolean overpaymentEquals(Amount amount) {
        return this.overpayment.equals(amount);
    }

    private boolean overpaymentIsHigherThan(Amount amount) {
        return this.overpayment.isHigherThan(amount);
    }

}