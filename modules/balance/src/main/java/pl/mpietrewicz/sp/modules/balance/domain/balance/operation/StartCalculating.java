package pl.mpietrewicz.sp.modules.balance.domain.balance.operation;

import lombok.NoArgsConstructor;
import pl.mpietrewicz.sp.ddd.annotations.domain.ValueObject;
import pl.mpietrewicz.sp.ddd.canonicalmodel.publishedlanguage.AggregateId;
import pl.mpietrewicz.sp.ddd.canonicalmodel.publishedlanguage.Frequency;
import pl.mpietrewicz.sp.modules.balance.domain.balance.AccountingMonth;
import pl.mpietrewicz.sp.modules.balance.domain.balance.Operation;
import pl.mpietrewicz.sp.modules.balance.domain.balance.Period;
import pl.mpietrewicz.sp.modules.balance.domain.balance.month.ComponentPremium;
import pl.mpietrewicz.sp.modules.balance.domain.balance.month.Month;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import java.math.BigDecimal;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;

import static java.math.BigDecimal.ZERO;
import static pl.mpietrewicz.sp.modules.balance.domain.balance.OperationType.START_CALCULATING;
import static pl.mpietrewicz.sp.modules.balance.domain.balance.month.MonthStatus.UNPAID;

@ValueObject
@Entity
@DiscriminatorValue("START_CONTRACT")
@NoArgsConstructor
public class StartCalculating extends Operation {

    private BigDecimal premium;
    private AggregateId componentId;

    public StartCalculating(YearMonth from, BigDecimal premium, AggregateId componentId) {
        super(from.atDay(1));
        this.premium = premium;
        this.componentId = componentId;
        this.type = START_CALCULATING;
        this.pending = false;
    }

    @Override
    public void calculate() {
        YearMonth from = getFrom();
        List<Month> months = createMonths(from);
        period = new Period(months);
    }

    private List<Month> createMonths(YearMonth from) {
        List<Month> months = new ArrayList<>();

        List<ComponentPremium> componentPremiums = new ArrayList<>();
        componentPremiums.add(new ComponentPremium(componentId, premium));
        AccountingMonth accountingMonth = new AccountingMonth(from);

        Month next = null;
        Month previous = null;
        for (YearMonth yearMonth : Frequency.QUARTERLY.getMonths(from)) { // todo: powinienem dodać tyle ile jest grace, a nie tyle ile w Frequency
            next = new Month(yearMonth, accountingMonth, UNPAID, ZERO, ZERO, previous, componentPremiums);
            if (previous != null) previous.setNext(next);
            previous = next;
            months.add(previous);
        }
        return months;
    }

    public YearMonth getFrom() {
        return YearMonth.from(date);
    }

}