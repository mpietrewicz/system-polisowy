package pl.mpietrewicz.sp.modules.oldbalance.domain.balance.period.state;

import lombok.NoArgsConstructor;
import pl.mpietrewicz.sp.modules.oldbalance.domain.balance.period.Period;
import pl.mpietrewicz.sp.modules.oldbalance.domain.balance.period.PeriodState;

import javax.persistence.Entity;
import java.math.BigDecimal;

import static java.math.BigDecimal.ZERO;
import static pl.mpietrewicz.sp.modules.oldbalance.domain.balance.period.PeriodStatus.UNDERPAID;
import static pl.mpietrewicz.sp.modules.oldbalance.domain.balance.period.PeriodStatus.UNPAID;

@Entity
@NoArgsConstructor
public class Underpaid extends PeriodState {

    public Underpaid(Period period, BigDecimal underpayment) {
        super(period, UNDERPAID, underpayment, ZERO);
    }

    @Override
    public void pay(BigDecimal payment) {
        BigDecimal underpayment = getUnderpayment();

        if (payment.compareTo(underpayment) > 0) {
            if (period.getNext().isPresent()) {
                period.changeState(new Paid(period));
                period.getNext().get().tryPay(payment.subtract(underpayment));
            } else {
                period.changeState(new Overpaid(period, payment.subtract(underpayment)));
            }
        } else if (payment.compareTo(underpayment) == 0) {
            period.changeState(new Paid(period));
        } else if (payment.compareTo(underpayment) < 0) {
            decreaseUnderpayment(payment);
        }
    }

    @Override
    public void refund(BigDecimal refund) {
        BigDecimal paid = period.getPremiumDue().subtract(getUnderpayment());

        if (refund.compareTo(paid) > 0) {
            if (period.getPrevious().isPresent()) {
                period.changeState(new Unpaid(period));
                period.getPrevious().get().tryRefund(refund.subtract(paid));
            } else {
                throw new IllegalStateException("You're trying to refund more than you have!");
            }
        } else if (refund.compareTo(paid) == 0) {
            period.changeState(new Unpaid(period));
        } else if (refund.compareTo(paid) < 0) {
            increaseUnderpayment(refund);
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