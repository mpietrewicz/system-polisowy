package pl.mpietrewicz.sp.modules.balance.domain.balance.operation;

import lombok.NoArgsConstructor;
import pl.mpietrewicz.sp.ddd.annotations.domain.ValueObject;
import pl.mpietrewicz.sp.modules.balance.domain.balance.Operation;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import java.time.LocalDate;
import java.time.YearMonth;

import static pl.mpietrewicz.sp.modules.balance.domain.balance.OperationType.STOP_CALCULATING;

@ValueObject
@Entity
@DiscriminatorValue("STOP_CALCULATING")
@NoArgsConstructor
public class StopCalculating extends Operation {

    public StopCalculating(LocalDate date) {
        super(date);
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

}