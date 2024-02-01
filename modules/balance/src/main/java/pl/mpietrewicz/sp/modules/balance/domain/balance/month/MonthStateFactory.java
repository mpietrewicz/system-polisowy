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

    public static MonthState createState(PaidStatus paidStatus, Amount paid) {
        if (paidStatus == PaidStatus.UNPAID) {
            return new Unpaid();
        } else if (paidStatus == PaidStatus.UNDERPAID) {
            return new Underpaid(paid);
        } else if (paidStatus == PaidStatus.PAID) {
            return new Paid(paid);
        } else if (paidStatus == PaidStatus.OVERPAID) {
            return new Overpaid(paid);
        } else {
            throw new IllegalStateException("Illegal month state!");
        }
    }

}