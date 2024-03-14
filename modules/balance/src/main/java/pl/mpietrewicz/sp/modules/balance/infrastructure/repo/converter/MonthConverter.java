package pl.mpietrewicz.sp.modules.balance.infrastructure.repo.converter;

import org.springframework.stereotype.Component;
import pl.mpietrewicz.sp.ddd.sharedkernel.Amount;
import pl.mpietrewicz.sp.modules.balance.domain.balance.month.Month;
import pl.mpietrewicz.sp.modules.balance.domain.balance.month.MonthState;
import pl.mpietrewicz.sp.modules.balance.domain.balance.month.PaidStatus;
import pl.mpietrewicz.sp.modules.balance.domain.balance.month.state.Paid;
import pl.mpietrewicz.sp.modules.balance.domain.balance.month.state.Underpaid;
import pl.mpietrewicz.sp.modules.balance.domain.balance.month.state.Unpaid;
import pl.mpietrewicz.sp.modules.balance.infrastructure.repo.entity.MonthEntity;

import java.time.YearMonth;

@Component
public class MonthConverter {

    public Month convert(MonthEntity entity) {
        Month month = new Month(entity.getEntityId(), YearMonth.from(entity.getYearMonth()), new Amount(entity.getPremium()), entity.isRenewal());
        MonthState monthState = createMonthState(entity.getPaidStatus(), month, new Amount(entity.getPaid()));
        month.changeState(monthState);
        return month;
    }

    private MonthState createMonthState(PaidStatus paidStatus, Month month, Amount paid) {
        switch (paidStatus) {
            case PAID:
                return new Paid(month, paid);
            case UNDERPAID:
                return new Underpaid(month, paid);
            case UNPAID:
                return new Unpaid(month);
            default:
                throw new IllegalStateException();
        }
    }

    public MonthEntity convert(Month model) {
        return new MonthEntity(model.getId(), model.getYearMonth(), model.getPremium().getBigDecimal(),
                model.getPaidStatus(), model.getPaid().getBigDecimal(), model.isRenewal());
    }

}