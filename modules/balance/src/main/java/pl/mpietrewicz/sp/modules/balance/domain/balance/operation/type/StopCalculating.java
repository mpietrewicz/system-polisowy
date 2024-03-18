package pl.mpietrewicz.sp.modules.balance.domain.balance.operation.type;

import lombok.NoArgsConstructor;
import pl.mpietrewicz.sp.ddd.annotations.domain.ValueObject;
import pl.mpietrewicz.sp.ddd.sharedkernel.Amount;
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

import static pl.mpietrewicz.sp.modules.balance.domain.balance.operation.OperationType.STOP_CALCULATING;

@ValueObject
@Entity
@DiscriminatorValue("STOP_CALCULATING")
@NoArgsConstructor
public class StopCalculating extends Operation implements StopCalculatingService {

    @Embedded
    @AttributeOverride(name = "value", column = @Column(name = "excess"))
    private Amount excess;

    private LocalDate end;

    private boolean valid;

    public StopCalculating(LocalDate end, Balance balance) {
        super(LocalDateTime.now(), balance, STOP_CALCULATING);
        this.end = end;
        this.valid = true;
    }

    @Override
    public void execute() {
        YearMonth monthOfEnd = YearMonth.from(end);
        Period period = getPeriod();
        this.excess = period.refundUpTo(monthOfEnd);
    }

    @Override
    protected void reexecute(LocalDateTime registration) {
        if (isValid()) {
            execute();
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
    public void invalidate() {
        valid = false;
    }

    @Override
    public LocalDate getEnd() {
        return end;
    }

    private int orderAlwaysLast(Operation operation) {
        return this == operation
                ? 0
                : 1;
    }

}