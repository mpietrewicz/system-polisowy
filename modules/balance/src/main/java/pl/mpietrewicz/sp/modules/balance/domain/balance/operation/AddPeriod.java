package pl.mpietrewicz.sp.modules.balance.domain.balance.operation;

import lombok.NoArgsConstructor;
import pl.mpietrewicz.sp.ddd.annotations.domain.ValueObject;
import pl.mpietrewicz.sp.ddd.canonicalmodel.publishedlanguage.Frequency;
import pl.mpietrewicz.sp.modules.balance.domain.balance.Operation;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.Optional;

import static pl.mpietrewicz.sp.modules.balance.domain.balance.OperationType.ADD_PERIOD;

@ValueObject
@Entity
@DiscriminatorValue("ADD_PERIOD")
@NoArgsConstructor
public class AddPeriod extends Operation {

    private Frequency frequency;

    public AddPeriod(YearMonth date, Frequency frequency) {
        super(date.atDay(1));
        this.frequency = frequency;
        this.type = ADD_PERIOD;
    }

    public AddPeriod(LocalDateTime registration, YearMonth date, Frequency frequency) {
        super(registration, date.atDay(1));
        this.frequency = frequency;
        this.type = ADD_PERIOD;
    }

    @Override
    protected void calculate() {
        YearMonth startMonth = YearMonth.from(date);
        if (isPeriodMonthCorrect(startMonth)) {
            frequency.getMonths(startMonth)
                    .forEach(m -> period.addMonth());
        } else {
            throw new IllegalStateException();
        }

    }

    @Override
    public Optional<Frequency> getFrequency() {
        return Optional.of(frequency);
    }

    @Override
    protected Integer getPriority() {
        return 0;
    }

    private boolean isPeriodMonthCorrect(YearMonth startMonth) {
        return period.getLastMonth().getYearMonth().plusMonths(1).equals(startMonth);
    }

}