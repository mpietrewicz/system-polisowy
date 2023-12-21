package pl.mpietrewicz.sp.modules.balance.domain.balance.month.state;

import lombok.NoArgsConstructor;
import pl.mpietrewicz.sp.modules.balance.domain.balance.month.ComponentPremium;
import pl.mpietrewicz.sp.modules.balance.domain.balance.month.Month;
import pl.mpietrewicz.sp.modules.balance.domain.balance.month.MonthState;

import javax.persistence.Entity;
import java.math.BigDecimal;
import java.util.List;

import static java.math.BigDecimal.ZERO;
import static pl.mpietrewicz.sp.modules.balance.domain.balance.month.MonthStatus.UNDERPAID;
import static pl.mpietrewicz.sp.modules.balance.domain.balance.month.MonthStatus.UNPAID;

@Entity
@NoArgsConstructor
public class Underpaid extends MonthState {

    public Underpaid(Month month, BigDecimal underpayment) {
        super(month, UNDERPAID, underpayment, ZERO);
    }

    @Override
    public void pay(BigDecimal payment) {
        BigDecimal underpayment = getUnderpayment();

        if (payment.compareTo(underpayment) > 0) {
            if (month.getNext().isPresent()) {
                month.changeState(new Paid(month));
                month.getNext().get().tryPay(payment.subtract(underpayment));
            } else {
                month.changeState(new Overpaid(month, payment.subtract(underpayment)));
            }
        } else if (payment.compareTo(underpayment) == 0) {
            month.changeState(new Paid(month));
        } else if (payment.compareTo(underpayment) < 0) {
            decreaseUnderpayment(payment);
        }
    }

    @Override
    public void refund(BigDecimal refund) {
        BigDecimal paid = month.getPremium().subtract(getUnderpayment());

        if (refund.compareTo(paid) > 0) {
            if (month.getPrevious().isPresent()) {
                month.changeState(new Unpaid(month));
                month.getPrevious().get().tryRefund(refund.subtract(paid));
            } else {
                throw new IllegalStateException("You're trying to refund more than you have!");
            }
        } else if (refund.compareTo(paid) == 0) {
            month.changeState(new Unpaid(month));
        } else if (refund.compareTo(paid) < 0) {
            increaseUnderpayment(refund);
        }
    }

    @Override
    public Month createNextMonth(List<ComponentPremium> componentPremiums) {
        Month previous = this.month;
        Month next = new Month(
                previous.getYearMonth().plusMonths(1),
                UNPAID,
                ZERO,
                ZERO,
                previous,
                componentPremiums
        );
        previous.setNext(next);
        return next;
    }

    @Override
    public BigDecimal getPaid() {
        return month.getPremium().subtract(getUnderpayment());
    }

}