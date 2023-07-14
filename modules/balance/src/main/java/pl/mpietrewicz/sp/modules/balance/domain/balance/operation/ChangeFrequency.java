package pl.mpietrewicz.sp.modules.balance.domain.balance.operation;

import lombok.NoArgsConstructor;
import pl.mpietrewicz.sp.ddd.annotations.domain.ValueObject;
import pl.mpietrewicz.sp.ddd.canonicalmodel.publishedlanguage.Frequency;
import pl.mpietrewicz.sp.modules.balance.domain.balance.Operation;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.Optional;

import static pl.mpietrewicz.sp.modules.balance.domain.balance.OperationType.CHANGE_FREQUENCY;

@ValueObject
@Entity
@DiscriminatorValue("CHANGE_FREQUENCY")
@NoArgsConstructor
public class ChangeFrequency extends Operation {

    private Frequency frequency;

    public ChangeFrequency(LocalDate date, Frequency frequency) {
        super(date);
        this.frequency = frequency;
        this.type = CHANGE_FREQUENCY;
    }

    public ChangeFrequency(LocalDateTime registration, LocalDate date, Frequency frequency) {
        super(registration, date);
        this.frequency = frequency;
        this.type = CHANGE_FREQUENCY;
    }

    @Override
    public void calculate() {
        YearMonth startMonth = YearMonth.from(date);
        period.deleteMonths(startMonth);
        frequency.getMonths(startMonth)
                .forEach(m -> period.addMonth());
    }

    @Override
    public Optional<Frequency> getFrequency() {
        return Optional.of(frequency);
    }

}