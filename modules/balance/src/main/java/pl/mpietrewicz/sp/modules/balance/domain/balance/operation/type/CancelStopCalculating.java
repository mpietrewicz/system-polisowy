package pl.mpietrewicz.sp.modules.balance.domain.balance.operation.type;

import lombok.NoArgsConstructor;
import pl.mpietrewicz.sp.ddd.annotations.domain.ValueObject;
import pl.mpietrewicz.sp.modules.balance.domain.balance.Balance;
import pl.mpietrewicz.sp.modules.balance.domain.balance.operation.Operation;
import pl.mpietrewicz.sp.modules.balance.domain.balance.operation.OperationType;
import pl.mpietrewicz.sp.modules.balance.exceptions.ReexecutionException;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Transient;
import java.time.LocalDate;
import java.time.LocalDateTime;

import static pl.mpietrewicz.sp.modules.balance.domain.balance.operation.OperationType.CANCEL_STOP_CALCULATING;

@ValueObject
@Entity
@DiscriminatorValue("CANCEL_STOP_CALCULATING")
@NoArgsConstructor
public class CancelStopCalculating extends Operation {

    @Transient
    private StopCalculatingService stopCalculatingService;

    private LocalDate canceledEnd;

    private boolean valid;

    public CancelStopCalculating(StopCalculatingService stopCalculatingService, Balance balance) {
        super(LocalDateTime.now(), balance, CANCEL_STOP_CALCULATING);
        this.stopCalculatingService = stopCalculatingService;
        this.canceledEnd = stopCalculatingService.getEnd();
        this.valid = true;
    }

    @Override
    public void execute() {
        stopCalculatingService.invalidate();
        valid = false;
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
        return stopCalculatingService.orderComparator(operation);
    }

    @Override
    protected Integer getPriority() {
        return stopCalculatingService.getPriority() - 1;
    }

    @Override
    public boolean isValid() {
        return valid;
    }

}