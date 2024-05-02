package pl.mpietrewicz.sp.modules.balance.domain.balance.operation.type;

import lombok.NoArgsConstructor;
import pl.mpietrewicz.sp.ddd.annotations.domain.ValueObject;
import pl.mpietrewicz.sp.ddd.sharedkernel.valueobject.PositiveAmount;
import pl.mpietrewicz.sp.modules.balance.domain.balance.Balance;
import pl.mpietrewicz.sp.modules.balance.domain.balance.operation.Operation;
import pl.mpietrewicz.sp.modules.balance.domain.balance.operation.RequiredPeriod;
import pl.mpietrewicz.sp.modules.balance.domain.balance.period.PartialPeriod;
import pl.mpietrewicz.sp.modules.balance.domain.balance.period.Period;
import pl.mpietrewicz.sp.modules.balance.exceptions.ReexecutionException;

import javax.persistence.AttributeOverride;
import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.Transient;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.List;

import static pl.mpietrewicz.sp.modules.balance.domain.balance.operation.OperationType.START_CALCULATING;
import static pl.mpietrewicz.sp.modules.balance.domain.balance.operation.RequiredPeriod.NO_MONTHS;

@ValueObject
@Entity
@DiscriminatorValue("START_CALCULATING")
@NoArgsConstructor
public class StartCalculating extends Operation {

    @Embedded
    @AttributeOverride(name = "value", column = @Column(name = "premium"))
    private PositiveAmount premium;

    @Transient
    private static final RequiredPeriod requiredPeriod = NO_MONTHS;

    public StartCalculating(YearMonth start, PositiveAmount premium, List<PartialPeriod> partialPeriods, Balance balance) {
        super(start.atDay(1), LocalDateTime.now(), balance, START_CALCULATING, partialPeriods);
        this.premium = premium;
    }

    @Override
    protected void execute(Period period) {
        throw new UnsupportedOperationException();
    }

    @Override
    protected void reexecute(Period period, LocalDateTime registration) {
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

    @Override
    public RequiredPeriod getRequiredPeriod() {
        return requiredPeriod;
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