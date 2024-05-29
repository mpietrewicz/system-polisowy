package pl.mpietrewicz.sp.modules.balance.domain.balance.operation.type;

import lombok.NoArgsConstructor;
import pl.mpietrewicz.sp.ddd.annotations.domain.ValueObject;
import pl.mpietrewicz.sp.modules.balance.domain.balance.Balance;
import pl.mpietrewicz.sp.modules.balance.domain.balance.operation.Operation;
import pl.mpietrewicz.sp.modules.balance.domain.balance.operation.RequiredPeriod;
import pl.mpietrewicz.sp.modules.balance.domain.balance.period.Period;
import pl.mpietrewicz.sp.modules.balance.exceptions.ReexecutionException;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.Transient;
import java.time.LocalDate;
import java.time.LocalDateTime;

import static pl.mpietrewicz.sp.modules.balance.domain.balance.operation.OperationType.CANCEL_STOP_CALCULATING;
import static pl.mpietrewicz.sp.modules.balance.domain.balance.operation.RequiredPeriod.NO_MONTHS;

@ValueObject
@Entity
@DiscriminatorValue("CANCEL_STOP_CALCULATING")
@NoArgsConstructor
public class CancelStopCalculating extends Operation {

    private LocalDate canceledEnd;

    private boolean executed;

    @Transient
    private StopCalculatingService stopCalculatingService;

    @Transient
    private static final RequiredPeriod requiredPeriod = NO_MONTHS;

    public CancelStopCalculating(StopCalculatingService stopCalculatingService, Balance balance) {
        super(LocalDateTime.now(), balance, CANCEL_STOP_CALCULATING);
        this.stopCalculatingService = stopCalculatingService;
        this.canceledEnd = stopCalculatingService.getEnd();
        this.executed = false;
    }

    @Override
    public void execute(Period period) {
        stopCalculatingService.invalidate();
        executed = true;
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
        return stopCalculatingService.orderComparator(operation);
    }

    @Override
    protected Integer getPriority() {
        return stopCalculatingService.getPriority() - 1;
    }

    @Override
    public boolean isValid() {
        return !executed;
    }

    @Override
    public RequiredPeriod getRequiredPeriod() {
        return requiredPeriod;
    }

}