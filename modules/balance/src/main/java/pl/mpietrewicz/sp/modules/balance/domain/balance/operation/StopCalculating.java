package pl.mpietrewicz.sp.modules.balance.domain.balance.operation;

import lombok.NoArgsConstructor;
import pl.mpietrewicz.sp.ddd.annotations.domain.ValueObject;
import pl.mpietrewicz.sp.ddd.canonicalmodel.publishedlanguage.Frequency;
import pl.mpietrewicz.sp.modules.balance.domain.balance.Operation;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.Optional;

import static pl.mpietrewicz.sp.modules.balance.domain.balance.OperationType.STOP_CALCULATING;

@ValueObject
@Entity
@DiscriminatorValue("STOP_CALCULATING")
@NoArgsConstructor
public class StopCalculating extends Operation {

    private Frequency frequency;

    public StopCalculating(LocalDate date, Frequency frequency) {
        super(date);
        this.frequency = frequency;
        this.type = STOP_CALCULATING;
    }

    @Override
    public void calculate(Operation previousOperation) {
        this.period = previousOperation.getPeriodCopy();
        calculate();
        this.pending = false;
    }

    @Override
    public void calculate() {
        period.deleteMonths(YearMonth.from(date).plusMonths(1));
    }

    @Override
    public Optional<Frequency> getFrequency() {
        return Optional.of(frequency);
    }

}