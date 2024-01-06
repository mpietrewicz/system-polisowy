package pl.mpietrewicz.sp.modules.balance.domain.balance.month;

import pl.mpietrewicz.sp.ddd.annotations.domain.StaticDomainFactory;
import pl.mpietrewicz.sp.ddd.sharedkernel.Amount;
import pl.mpietrewicz.sp.modules.balance.domain.balance.month.state.Overpaid;
import pl.mpietrewicz.sp.modules.balance.domain.balance.month.state.Paid;
import pl.mpietrewicz.sp.modules.balance.domain.balance.month.state.Underpaid;
import pl.mpietrewicz.sp.modules.balance.domain.balance.month.state.Unpaid;

@StaticDomainFactory
public class MonthStateFactory {

    private MonthStateFactory() {
        throw new IllegalStateException("Factory (utility) class");
    }

    public static MonthState createState(Month month, MonthStatus status, Amount paid) {
        if (status == MonthStatus.UNPAID) {
            return new Unpaid(month);
        } else if (status == MonthStatus.UNDERPAID) {
            return new Underpaid(month, paid);
        } else if (status == MonthStatus.PAID) {
            return new Paid(month);
        } else if (status == MonthStatus.OVERPAID) {
            return new Overpaid(month, paid);
        } else {
            throw new IllegalStateException("Illegal month state!");
        }
    }

}