package pl.mpietrewicz.sp.modules.balance.domain.balance.operation.type;

import lombok.NoArgsConstructor;
import pl.mpietrewicz.sp.ddd.annotations.domain.ValueObject;
import pl.mpietrewicz.sp.ddd.sharedkernel.converter.AmountConverter;
import pl.mpietrewicz.sp.ddd.sharedkernel.valueobject.Amount;
import pl.mpietrewicz.sp.modules.balance.domain.balance.Balance;
import pl.mpietrewicz.sp.modules.balance.domain.balance.operation.Operation;
import pl.mpietrewicz.sp.modules.balance.domain.balance.operation.RequiredPeriod;
import pl.mpietrewicz.sp.modules.balance.domain.balance.period.Period;
import pl.mpietrewicz.sp.modules.balance.exceptions.ReexecutionException;

import javax.persistence.Convert;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.Transient;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;

import static pl.mpietrewicz.sp.modules.balance.domain.balance.operation.OperationType.STOP_CALCULATING;
import static pl.mpietrewicz.sp.modules.balance.domain.balance.operation.RequiredPeriod.ALL_MONTHS;

@ValueObject
@Entity
@DiscriminatorValue("STOP_CALCULATING")
@NoArgsConstructor
public class StopCalculating extends Operation implements StopCalculatingService {

    @Convert(converter = AmountConverter.class)
    private Amount excess;

    private LocalDate end;

    private boolean valid;

    @Transient
    private static final RequiredPeriod requiredPeriod = ALL_MONTHS;

    public StopCalculating(LocalDate end, Balance balance) {
        super(LocalDateTime.now(), balance, STOP_CALCULATING);
        this.end = end;
        this.valid = false;
    }

    @Override
    public void execute(Period period) {
        YearMonth monthOfEnd = YearMonth.from(end).plusMonths(1);
        this.excess = period.refundUpTo(monthOfEnd);
        this.valid = true;
    }

    @Override
    protected void reexecute(Period period, LocalDateTime registration) {
        if (valid) {
            execute(period);
        } else {
            throw new UnsupportedOperationException();
        }
    }

    @Override
    public int orderComparator(Operation operation) {
        return orderAlwaysLast(operation);
    }

    @Override
    public Integer getPriority() {
        return 30;
    }

    @Override
    public void publishFailedEvent(ReexecutionException exception) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean isValid() {
        return valid;
    }

    @Override
    public RequiredPeriod getRequiredPeriod() {
        return requiredPeriod;
    }

    @Override
    public void invalidate() {
        valid = false;
    }

    @Override
    public LocalDate getEnd() {
        return end;
    }

    public Amount getExcess() {
        return excess;
    }

    private int orderAlwaysLast(Operation operation) {
        return this == operation
                ? 0
                : 1;
    }

}