package pl.mpietrewicz.sp.modules.balance.domain.balance.month.state;

import lombok.NoArgsConstructor;
import pl.mpietrewicz.sp.modules.balance.domain.balance.month.Month;
import pl.mpietrewicz.sp.modules.balance.domain.balance.month.MonthState;

import javax.persistence.Entity;
import java.math.BigDecimal;

import static java.math.BigDecimal.ZERO;
import static pl.mpietrewicz.sp.modules.balance.domain.balance.month.MonthStatus.OVERPAID;
import static pl.mpietrewicz.sp.modules.balance.domain.balance.month.MonthStatus.PAID;
import static pl.mpietrewicz.sp.modules.balance.domain.balance.month.MonthStatus.UNDERPAID;

@Entity
@NoArgsConstructor
public class Overpaid extends MonthState {

    public Overpaid(Month month, BigDecimal overpayment) {
        super(month, OVERPAID, ZERO, overpayment);
    }

    public void pay(BigDecimal payment) {
        if (month.getNext().isPresent()) {
            throw new RuntimeException("The overpaid month is not last!");
        } else {
            increaseOverpayment(payment);
        }
    }

    public void refund(BigDecimal refund) {
        BigDecimal overpayment = getOverpayment();
        BigDecimal paid = month.getPremium().add(overpayment);

        if (refund.compareTo(paid) > 0) {
            if (month.getPrevious().isPresent()) {
                month.changeState(new Unpaid(month));
                month.getPrevious().get().tryRefund(refund.subtract(paid));
            } else {
                throw new RuntimeException("You're trying to refund more than you have!");
            }
        } else if (refund.compareTo(paid) == 0) {
            month.changeState(new Unpaid(month));
        } else if (refund.compareTo(paid) < 0) {

            if (refund.compareTo(overpayment) > 0) {
                month.changeState(new Underpaid(month, refund.subtract(overpayment)));
            } else if (refund.compareTo(overpayment) == 0) {
                month.changeState(new Paid(month));
            } else if (refund.compareTo(overpayment) < 0 ) {
                decreaseOverpayment(refund);
            }

        }
    }

    @Override
    public Month createNextMonth(BigDecimal premium) {
        Month previous = this.month;
        BigDecimal overpayment = getOverpayment();

        previous.changeState(new Paid(month));

        if (overpayment.compareTo(premium) > 0) {
            return createOverpaid(overpayment.subtract(premium), previous, premium);
        } else if (overpayment.compareTo(premium) == 0) {
            return createPaid(previous, premium);
        } else {
            return createUnderpaid(premium.subtract(overpayment), previous, premium);
        }
    }

    @Override
    public BigDecimal getPaid() {
        return getOverpayment();
    }

    private Month createOverpaid(BigDecimal overpayment, Month previous, BigDecimal premium) {
        return new Month(previous.getYearMonth().plusMonths(1), premium,
                OVERPAID, ZERO, overpayment, previous);
    }

    private Month createPaid(Month previous, BigDecimal premium) {
        return new Month(previous.getYearMonth().plusMonths(1), premium,
                PAID, ZERO, ZERO, previous);
    }

    private Month createUnderpaid(BigDecimal underpayment, Month previous, BigDecimal premium) {
        return new Month(previous.getYearMonth().plusMonths(1), premium,
                UNDERPAID, underpayment, ZERO, previous);
    }

}