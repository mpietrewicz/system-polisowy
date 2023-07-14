package pl.mpietrewicz.sp.modules.balance.domain.balance.operation;

import lombok.NoArgsConstructor;
import pl.mpietrewicz.sp.ddd.annotations.domain.ValueObject;
import pl.mpietrewicz.sp.modules.balance.domain.balance.Operation;
import pl.mpietrewicz.sp.modules.balance.domain.balance.month.Month;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;

import static pl.mpietrewicz.sp.modules.balance.domain.balance.OperationType.DELETE_PREMIUM;

@ValueObject
@Entity
@DiscriminatorValue("DELETE_PREMIUM")
@NoArgsConstructor
public class DeletePremium extends Operation {

    BigDecimal premium;

    public DeletePremium(LocalDate date, BigDecimal premium) {
        super(date);
        this.premium = premium;
        this.type = DELETE_PREMIUM;
    }

    @Override
    public void calculate() {
        YearMonth lastMonth = period.getLastMonth().getYearMonth();
        period.deleteMonths(YearMonth.from(date));
        Month month = period.getLastMonth();

        BigDecimal newPremium = month.getPremium().subtract(premium);
        if (newPremium.signum() < 0) {
            throw new IllegalStateException("Zmiana składki na miejszą niż zero");
        }

        while (period.getLastMonth().getYearMonth().compareTo(lastMonth) < 0) {
            period.addMonth(newPremium);
        }
    }

}