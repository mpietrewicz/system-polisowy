package pl.mpietrewicz.sp.modules.balance.domain.balance.operation.type;

import lombok.Getter;
import pl.mpietrewicz.sp.ddd.canonicalmodel.publishedlanguage.AggregateId;
import pl.mpietrewicz.sp.ddd.sharedkernel.Amount;
import pl.mpietrewicz.sp.modules.balance.domain.balance.Period;
import pl.mpietrewicz.sp.modules.balance.domain.balance.operation.Operation;
import pl.mpietrewicz.sp.modules.balance.domain.balance.operation.OperationType;

import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.List;

import static pl.mpietrewicz.sp.modules.balance.domain.balance.operation.OperationType.START_CALCULATING;

@Getter
public class StartCalculating extends Operation {

    private static final OperationType operationType = START_CALCULATING;

    private final Amount premium;

    public StartCalculating(YearMonth start, Amount premium, Period period) {
        super(start.atDay(1), null);
        this.premium = premium;
        this.periods.add(period);
    }

    public StartCalculating(Long id, YearMonth start, LocalDateTime registration, Amount premium, List<Period> periods) {
        super(id, start.atDay(1), registration, periods);
        this.premium = premium;
    }

    @Override
    protected void execute(AggregateId contractId) {
        throw new UnsupportedOperationException();
    }

    @Override
    protected void reexecute(AggregateId contractId, LocalDateTime registration) {
        throw new UnsupportedOperationException();
    }

    @Override
    protected void publishFailedEvent(Exception e) {
        throw new UnsupportedOperationException();
    }

    @Override
    public OperationType getOperationType() {
        return operationType;
    }

    @Override
    public int orderComparator(Operation operation) {
        return orderAlwaysFirst(operation);
    }

    private int orderAlwaysFirst(Operation operation) {
        return this == operation
                ? 0
                : -1;
    }

}