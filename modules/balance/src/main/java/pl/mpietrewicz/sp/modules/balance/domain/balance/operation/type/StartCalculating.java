package pl.mpietrewicz.sp.modules.balance.domain.balance.operation.type;

import lombok.NoArgsConstructor;
import pl.mpietrewicz.sp.ddd.annotations.domain.ValueObject;
import pl.mpietrewicz.sp.ddd.sharedkernel.valueobject.Amount;
import pl.mpietrewicz.sp.modules.balance.domain.balance.Balance;
import pl.mpietrewicz.sp.modules.balance.domain.balance.Period;
import pl.mpietrewicz.sp.modules.balance.domain.balance.operation.Operation;
import pl.mpietrewicz.sp.modules.balance.exceptions.ReexecutionException;

import javax.persistence.AttributeOverride;
import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;

import static pl.mpietrewicz.sp.modules.balance.domain.balance.operation.OperationType.START_CALCULATING;

@ValueObject
@Entity
@DiscriminatorValue("START_CALCULATING")
@NoArgsConstructor
public class StartCalculating extends Operation {

    @Embedded
    @AttributeOverride(name = "value", column = @Column(name = "premium"))
    private Amount premium;

    public StartCalculating(YearMonth start, Amount premium, Period period, Balance balance) {
        super(start.atDay(1), LocalDateTime.now(), balance, START_CALCULATING);
        this.premium = premium;
        this.periods.add(period);
    }

    @Override
    protected void execute() {
        throw new UnsupportedOperationException();
    }

    @Override
    protected void reexecute(LocalDateTime registration) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void publishFailedEvent(ReexecutionException exception) {
        throw new UnsupportedOperationException();
    }

    @Override
    public int orderComparator(Operation operation) {
        return orderAlwaysFirst(operation);
    }

    public LocalDate getDate() {
        return date;
    }

    private int orderAlwaysFirst(Operation operation) {
        return this == operation
                ? 0
                : -1;
    }

}