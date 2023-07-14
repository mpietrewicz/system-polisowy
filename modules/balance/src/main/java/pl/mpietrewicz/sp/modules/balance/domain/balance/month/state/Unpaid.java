package pl.mpietrewicz.sp.modules.balance.domain.balance.month.state;

import lombok.NoArgsConstructor;
import pl.mpietrewicz.sp.modules.balance.domain.balance.month.Month;
import pl.mpietrewicz.sp.modules.balance.domain.balance.month.MonthState;
import pl.mpietrewicz.sp.modules.balance.domain.balance.month.MonthStatus;

import javax.persistence.Entity;
import java.math.BigDecimal;

import static java.math.BigDecimal.ZERO;

@Entity
@NoArgsConstructor
public class Unpaid extends MonthState {

    public Unpaid(Month month) {
        super(month, MonthStatus.UNPAID, ZERO, ZERO);
    }

    @Override
    public void pay(BigDecimal payment) {
        BigDecimal premiumDue = month.getPremium();

        if (payment.compareTo(premiumDue) > 0) {
            if (month.getNext().isPresent()) {
                month.changeState(new Paid(month));
                month.getNext().get().tryPay(payment.subtract(premiumDue));
            } else {
                month.changeState(new Overpaid(month, payment.subtract(premiumDue)));
            }
        } else if (payment.compareTo(premiumDue) == 0) {
            month.changeState(new Paid(month));
        } else if (payment.compareTo(premiumDue) < 0) {
            month.changeState(new Underpaid(month, premiumDue.subtract(payment)));
        }
    }

    @Override
    public void refund(BigDecimal refund) {
        if (month.getPrevious().isPresent()) {
            month.getPrevious().get().tryRefund(refund);
        } else {
            throw new IllegalStateException("Nie można zwrócić więcej niż sauma kwot");
        }
    }

    @Override
    public Month createNextMonth(BigDecimal premium) {
        Month previous = this.month;
        Month next = new Month(
                previous.getYearMonth().plusMonths(1),
                premium,
                MonthStatus.UNPAID,
                ZERO,
                ZERO,
                previous
        );
        previous.setNext(next);
        return next;
    }

    @Override
    public BigDecimal getPaid() {
        return ZERO;
    }

}