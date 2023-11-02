package pl.mpietrewicz.sp.modules.balance.domain.balance.operation;

import lombok.NoArgsConstructor;
import pl.mpietrewicz.sp.ddd.annotations.domain.ValueObject;
import pl.mpietrewicz.sp.modules.balance.domain.balance.AccountingMonth;
import pl.mpietrewicz.sp.modules.balance.domain.balance.Operation;
import pl.mpietrewicz.sp.modules.balance.domain.balance.month.ComponentPremium;

import javax.persistence.CascadeType;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.OneToOne;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;

import static pl.mpietrewicz.sp.modules.balance.domain.balance.OperationType.CHANGE_PREMIUM;

@ValueObject
@Entity
@DiscriminatorValue("CHANGE_PREMIUM")
@NoArgsConstructor
public class ChangePremium extends Operation {

    @OneToOne(cascade = CascadeType.ALL)
    private ComponentPremium componentPremium;

    public ChangePremium(LocalDate date, ComponentPremium componentPremium) {
        super(date);
        this.componentPremium = componentPremium;
        this.type = CHANGE_PREMIUM;
    }

    @Override
    public void calculate() { // todo: może wypadać w okresie nie opłaconym
        AccountingMonth accountingMonth = new AccountingMonth(YearMonth.from(date)); // todo: raczej to nie będzie ta data
        List<YearMonth> deleteMonths = period.deleteMonths(YearMonth.from(date));
        deleteMonths.stream()
                .sorted(YearMonth::compareTo)
                .forEach(month -> period.addNextMonthWith(accountingMonth, componentPremium));
    }

}