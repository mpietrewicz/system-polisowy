package pl.mpietrewicz.sp.modules.balance.domain.balance.operation;

import lombok.NoArgsConstructor;
import pl.mpietrewicz.sp.ddd.annotations.domain.ValueObject;
import pl.mpietrewicz.sp.modules.balance.domain.balance.Operation;
import pl.mpietrewicz.sp.modules.balance.domain.balance.month.Month;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;

import static pl.mpietrewicz.sp.modules.balance.domain.balance.OperationType.ADD_PREMIUM;

@ValueObject
@Entity
@DiscriminatorValue("ADD_PREMIUM")
@NoArgsConstructor
public class AddPremium extends Operation {

    BigDecimal premium;

    public AddPremium(LocalDate date, BigDecimal premium) {
        super(date);
        this.premium = premium;
        this.type = ADD_PREMIUM;
    }

    public AddPremium(LocalDateTime registration, LocalDate date, BigDecimal premium) {
        super(registration, date);
        this.premium = premium;
        this.type = ADD_PREMIUM;
    }

    @Override
    public void calculate() {
        YearMonth lastMonth = period.getLastMonth().getYearMonth();
        period.deleteMonths(YearMonth.from(date));
        Month month = period.getLastMonth();

        BigDecimal newPremium = month.getPremium().add(premium);

        while (period.getLastMonth().getYearMonth().compareTo(lastMonth) < 0) {
            period.addMonth(newPremium);
        }
    }

}