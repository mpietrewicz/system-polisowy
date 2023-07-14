package pl.mpietrewicz.sp.modules.balance.domain.balance.month;

import org.hibernate.procedure.NoSuchParameterException;
import pl.mpietrewicz.sp.modules.balance.domain.balance.month.state.Overpaid;
import pl.mpietrewicz.sp.modules.balance.domain.balance.month.state.Paid;
import pl.mpietrewicz.sp.modules.balance.domain.balance.month.state.Underpaid;
import pl.mpietrewicz.sp.modules.balance.domain.balance.month.state.Unpaid;

import java.math.BigDecimal;

public class MonthStateFactory {

    private MonthStateFactory() {
        throw new IllegalStateException("Factory (utility) class");
    }

    protected static MonthState createState(Month month, MonthStatus status,
                                            BigDecimal underpayment, BigDecimal overpayment) {
        if (status == MonthStatus.UNPAID) {
            return new Unpaid(month);
        } else if (status == MonthStatus.UNDERPAID) {
            if (underpayment == null) throw new NoSuchParameterException("No underpayment to create underpaid month state");
            return new Underpaid(month, underpayment);
        } else if (status == MonthStatus.PAID) {
            return new Paid(month);
        } else if (status == MonthStatus.OVERPAID) {
            if (underpayment == null) throw new NoSuchParameterException("No overpayment to create overpaid month state");
            return new Overpaid(month, overpayment);
        } else {
            throw new IllegalStateException("Illegal month state!");
        }

    }

}