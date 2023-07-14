package pl.mpietrewicz.sp.modules.oldbalance.domain.balance.period;

import org.hibernate.procedure.NoSuchParameterException;
import pl.mpietrewicz.sp.modules.oldbalance.domain.balance.period.state.Overpaid;
import pl.mpietrewicz.sp.modules.oldbalance.domain.balance.period.state.Paid;
import pl.mpietrewicz.sp.modules.oldbalance.domain.balance.period.state.Underpaid;
import pl.mpietrewicz.sp.modules.oldbalance.domain.balance.period.state.Unpaid;

import java.math.BigDecimal;

import static pl.mpietrewicz.sp.modules.oldbalance.domain.balance.period.PeriodStatus.OVERPAID;
import static pl.mpietrewicz.sp.modules.oldbalance.domain.balance.period.PeriodStatus.PAID;
import static pl.mpietrewicz.sp.modules.oldbalance.domain.balance.period.PeriodStatus.UNDERPAID;
import static pl.mpietrewicz.sp.modules.oldbalance.domain.balance.period.PeriodStatus.UNPAID;

public class PeriodStateFactory {

    private PeriodStateFactory() {
        throw new IllegalStateException("Factory (utility) class");
    }

    public static PeriodState createState(Period period, PeriodStatus status,
                                          BigDecimal underpayment, BigDecimal overpayment) {
        if (status == UNPAID) {
            return new Unpaid(period);
        } else if (status == UNDERPAID) {
            if (underpayment == null) throw new NoSuchParameterException("No underpayment to create underpaid period state");
            return new Underpaid(period, underpayment);
        } else if (status == PAID) {
            return new Paid(period);
        } else if (status == OVERPAID) {
            if (underpayment == null) throw new NoSuchParameterException("No overpayment to create overpaid period state");
            return new Overpaid(period, overpayment);
        } else {
            throw new IllegalStateException("Illegal period state!");
        }

    }

}