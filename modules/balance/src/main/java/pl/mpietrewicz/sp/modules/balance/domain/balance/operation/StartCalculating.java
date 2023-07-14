package pl.mpietrewicz.sp.modules.balance.domain.balance.operation;

import lombok.NoArgsConstructor;
import pl.mpietrewicz.sp.ddd.annotations.domain.ValueObject;
import pl.mpietrewicz.sp.ddd.canonicalmodel.publishedlanguage.Frequency;
import pl.mpietrewicz.sp.modules.balance.domain.balance.Operation;
import pl.mpietrewicz.sp.modules.balance.domain.balance.Period;
import pl.mpietrewicz.sp.modules.balance.domain.balance.month.Month;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static java.math.BigDecimal.ZERO;
import static pl.mpietrewicz.sp.modules.balance.domain.balance.OperationType.START_CONTRACT;
import static pl.mpietrewicz.sp.modules.balance.domain.balance.month.MonthStatus.UNPAID;

@ValueObject
@Entity
@DiscriminatorValue("START_CONTRACT")
@NoArgsConstructor
public class StartCalculating extends Operation {

    private Frequency frequency;
    private BigDecimal premium;

    public StartCalculating(YearMonth from, BigDecimal premium, Frequency frequency) {
        super(from.atDay(1));
        this.premium = premium;
        this.frequency = frequency;
        this.type = START_CONTRACT;
    }

    public StartCalculating(LocalDateTime registration, LocalDate date, BigDecimal premium, Frequency frequency) {
        super(registration, date);
        this.frequency = frequency;
        this.premium = premium;
        this.type = START_CONTRACT;
    }

    public List<Month> createMonths(YearMonth from) {
        List<Month> months = new ArrayList<>();
        Month month = null;
        for (YearMonth yearMonth : frequency.getMonths(from)) {
            if (month == null) {
                month = new Month(yearMonth, premium, UNPAID, ZERO, ZERO, null);
            } else {
                month = month.createNextMonth();
            }
            months.add(month);
        }
        return months;
    }

    @Override
    public void calculate() {
        YearMonth from = getFrom();
        if (period == null) {
            List<Month> months = createMonths(from);
            period = new Period(months);
        } else {
            throw new IllegalStateException("Nie można drugi raz zlecić start calculating!");
        }
    }

    @Override
    public Optional<Frequency> getFrequency() {
        return Optional.of(frequency);
    }

    public YearMonth getFrom() {
        return YearMonth.from(date);
    }

}