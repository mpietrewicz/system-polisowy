package pl.mpietrewicz.sp.modules.oldbalance.domain.balance.period.state;

import lombok.NoArgsConstructor;
import pl.mpietrewicz.sp.modules.oldbalance.domain.balance.period.Period;
import pl.mpietrewicz.sp.modules.oldbalance.domain.balance.period.PeriodState;

import javax.persistence.Entity;
import java.math.BigDecimal;

import static java.math.BigDecimal.ZERO;
import static pl.mpietrewicz.sp.modules.oldbalance.domain.balance.period.PeriodStatus.UNPAID;

@Entity
@NoArgsConstructor
public class Unpaid extends PeriodState {

    public Unpaid(Period period) {
        super(period, UNPAID, ZERO, ZERO);
    }

    @Override
    public void pay(BigDecimal payment) {
        BigDecimal premiumDue = period.getPremiumDue();

        if (payment.compareTo(premiumDue) > 0) {
            if (period.getNext().isPresent()) {
                period.changeState(new Paid(period));
                period.getNext().get().tryPay(payment.subtract(premiumDue));
            } else {
                period.changeState(new Overpaid(period, payment.subtract(premiumDue)));
            }
        } else if (payment.compareTo(premiumDue) == 0) {
            period.changeState(new Paid(period));
        } else if (payment.compareTo(premiumDue) < 0) {
            period.changeState(new Underpaid(period, premiumDue.subtract(payment)));
        }
    }

    @Override
    public void refund(BigDecimal refund) {
        period.getPrevious().ifPresent(
                previousPeriod -> previousPeriod.tryRefund(refund)
        );
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