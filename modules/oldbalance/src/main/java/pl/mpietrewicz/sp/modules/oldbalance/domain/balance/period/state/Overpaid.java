package pl.mpietrewicz.sp.modules.oldbalance.domain.balance.period.state;

import lombok.NoArgsConstructor;
import pl.mpietrewicz.sp.modules.oldbalance.domain.balance.period.Period;
import pl.mpietrewicz.sp.modules.oldbalance.domain.balance.period.PeriodState;

import javax.persistence.Entity;
import java.math.BigDecimal;

import static java.math.BigDecimal.ZERO;
import static pl.mpietrewicz.sp.modules.oldbalance.domain.balance.period.PeriodStatus.OVERPAID;
import static pl.mpietrewicz.sp.modules.oldbalance.domain.balance.period.PeriodStatus.PAID;
import static pl.mpietrewicz.sp.modules.oldbalance.domain.balance.period.PeriodStatus.UNDERPAID;

@Entity
@NoArgsConstructor
public class Overpaid extends PeriodState {

    public Overpaid(Period period, BigDecimal overpayment) {
        super(period, OVERPAID, ZERO, overpayment);
    }

    public void pay(BigDecimal payment) {
        if (period.getNext().isPresent()) {
            throw new RuntimeException("The overpaid period is not last!");
        } else {
            increaseOverpayment(payment);
        }
    }

    public void refund(BigDecimal refund) {
        BigDecimal overpayment = getOverpayment();
        BigDecimal paid = period.getPremiumDue().add(overpayment);

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

            if (refund.compareTo(overpayment) > 0) {
                period.changeState(new Underpaid(period, refund.subtract(overpayment)));
            } else if (refund.compareTo(overpayment) == 0) {
                period.changeState(new Paid(period));
            } else if (refund.compareTo(overpayment) < 0 ) {
                decreaseOverpayment(refund);
            }

        }
    }

    @Override
    public Period createNextPeriod() {
        Period previous = this.period;
        BigDecimal overpayment = getOverpayment();
        BigDecimal premiumDue = previous.getPremiumDue();

        previous.changeState(new Paid(period));

        if (overpayment.compareTo(premiumDue) > 0) {
            return createOverpaid(overpayment.subtract(premiumDue), previous);
        } else if (overpayment.compareTo(premiumDue) == 0) {
            return createPaid(previous);
        } else {
            return createUnderpaid(premiumDue.subtract(overpayment), previous);
        }
    }

    private Period createOverpaid(BigDecimal overpayment, Period previous) {
        return new Period(previous.getMonth().plusMonths(1), previous.getDue(),
                OVERPAID, ZERO, overpayment, previous);
    }

    private Period createPaid(Period previous) {
        return new Period(previous.getMonth().plusMonths(1), previous.getDue(),
                PAID, ZERO, ZERO, previous);
    }

    private Period createUnderpaid(BigDecimal underpayment, Period previous) {
        return new Period(previous.getMonth().plusMonths(1), previous.getDue(),
                UNDERPAID, underpayment, ZERO, previous);
    }

}