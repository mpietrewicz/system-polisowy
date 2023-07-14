package pl.mpietrewicz.sp.modules.balance.domain.balance.month.state;

import lombok.NoArgsConstructor;
import pl.mpietrewicz.sp.modules.balance.domain.balance.month.Month;
import pl.mpietrewicz.sp.modules.balance.domain.balance.month.MonthState;

import javax.persistence.Entity;
import java.math.BigDecimal;

import static java.math.BigDecimal.ZERO;
import static pl.mpietrewicz.sp.modules.balance.domain.balance.month.MonthStatus.PAID;
import static pl.mpietrewicz.sp.modules.balance.domain.balance.month.MonthStatus.UNPAID;

@Entity
@NoArgsConstructor
public class Paid extends MonthState {

    public Paid(Month month) {
        super(month, PAID, ZERO, ZERO);
    }

    @Override
    public void pay(BigDecimal payment) {
        if (month.getNext().isEmpty()) {
            month.changeState(new Overpaid(month, payment));
        } else {
            month.getNext().get().tryPay(payment);
        }
    }

    @Override
    public void refund(BigDecimal refund) {
        BigDecimal paid = month.getPremium();

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
            month.changeState(new Underpaid(month, refund));
        }
    }

    @Override
    public Month createNextMonth(BigDecimal premium) {
        Month previous = this.month;
        Month next = new Month(
                previous.getYearMonth().plusMonths(1),
                premium,
                UNPAID,
                ZERO,
                ZERO,
                previous
        );
        previous.setNext(next);
        return next;
    }

    @Override
    public BigDecimal getPaid() {
        return month.getPremium();
    }

}