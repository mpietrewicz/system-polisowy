package pl.mpietrewicz.sp.modules.oldbalance.domain.balance.period.state;

import lombok.NoArgsConstructor;
import pl.mpietrewicz.sp.modules.oldbalance.domain.balance.period.Period;
import pl.mpietrewicz.sp.modules.oldbalance.domain.balance.period.PeriodState;

import javax.persistence.Entity;
import java.math.BigDecimal;

import static java.math.BigDecimal.ZERO;
import static pl.mpietrewicz.sp.modules.oldbalance.domain.balance.period.PeriodStatus.PAID;
import static pl.mpietrewicz.sp.modules.oldbalance.domain.balance.period.PeriodStatus.UNPAID;

@Entity
@NoArgsConstructor
public class Paid extends PeriodState {

    public Paid(Period period) {
        super(period, PAID, ZERO, ZERO);
    }

    @Override
    public void pay(BigDecimal payment) {
        if (period.getNext().isEmpty()) {
            period.changeState(new Overpaid(period, payment));
        } else {
            period.getNext().get().tryPay(payment);
        }
    }

    @Override
    public void refund(BigDecimal refund) {
        BigDecimal paid = period.getPremiumDue();

        if (refund.compareTo(paid) > 0) {
            if (period.getPrevious().isPresent()) {
                period.changeState(new Unpaid(period));
                period.getPrevious().get().tryRefund(refund.subtract(paid));
            } else {
                throw new RuntimeException("You're trying to refund more than you have!");
            }
        } else if (refund.compareTo(paid) == 0) {
            period.changeState(new Unpaid(period));
        } else if (refund.compareTo(paid) < 0) {
            period.changeState(new Underpaid(period, paid.subtract(refund)));
        }
    }

    @Override
    public Period createNextPeriod() {
        Period previous = this.period;
        Period next = new Period(
                previous.getMonth().plusMonths(1),
                previous.getDue(),
                UNPAID,
                ZERO,
                ZERO,
                previous
        );
        previous.setNext(next);
        return next;
    }

}