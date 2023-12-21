package pl.mpietrewicz.sp.modules.balance.domain.balance.operation;

import lombok.NoArgsConstructor;
import pl.mpietrewicz.sp.ddd.annotations.domain.ValueObject;
import pl.mpietrewicz.sp.ddd.canonicalmodel.publishedlanguage.AggregateId;
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
    public void execute(Operation previousOperation, int grace) {
        List<Month> months = createMonths(grace);
        period = new Period(months);
        this.pending = false;
    }

    @Override
    public void execute() {
        throw new UnsupportedOperationException("Metoda nie obsługiwana w StartCalculating Operation");
    }

    private List<Month> createMonths(int grace) {
        List<Month> months = new ArrayList<>();

        List<ComponentPremium> componentPremiums = new ArrayList<>();
        componentPremiums.add(new ComponentPremium(componentId, premium));

        Month next = null;
        Month previous = null;
        for (int i = 0; i < grace; i++) {
            next = new Month(YearMonth.from(date).plusMonths(i), UNPAID, ZERO, ZERO, previous, componentPremiums);
            if (previous != null) previous.setNext(next);
            previous = next;
            months.add(previous);
        }

        return months;
    }

}