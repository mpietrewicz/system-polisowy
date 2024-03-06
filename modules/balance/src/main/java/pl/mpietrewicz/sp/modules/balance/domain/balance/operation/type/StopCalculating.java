package pl.mpietrewicz.sp.modules.balance.domain.balance.operation.type;

import lombok.Getter;
import pl.mpietrewicz.sp.ddd.canonicalmodel.publishedlanguage.snapshot.premium.PremiumSnapshot;
import pl.mpietrewicz.sp.ddd.sharedkernel.Amount;
import pl.mpietrewicz.sp.ddd.support.domain.DomainEventPublisher;
import pl.mpietrewicz.sp.modules.balance.domain.balance.Period;
import pl.mpietrewicz.sp.modules.balance.domain.balance.operation.Operation;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import java.util.stream.Stream;

import static pl.mpietrewicz.sp.modules.balance.domain.balance.operation.OperationType.STOP_CALCULATING;

@Getter
public class StopCalculating extends Operation {

    private Amount excess;
    private final LocalDate register;
    private final LocalDate end;

    public StopCalculating(LocalDate register, LocalDate end) {
        super(register);
        this.register = register;
        this.end = end;
        this.type = STOP_CALCULATING;
    }

    public StopCalculating(Long id, LocalDate register, LocalDate end, Amount excess, List<Period> periods) {
        super(id, register, periods);
        this.register = register;
        this.end = end;
        this.excess = excess;
        this.type = STOP_CALCULATING;
    }

    @Override
    public void execute(PremiumSnapshot premiumSnapshot, DomainEventPublisher eventPublisher) {
        YearMonth monthOfEnd = YearMonth.from(end);
        this.excess = getPeriod().tryRefundUpTo(monthOfEnd);
        // todo: albo liczę ile jeszcze okresów mam niedopłaty
    }

    @Override
    protected void reexecute(PremiumSnapshot premiumSnapshot, DomainEventPublisher eventPublisher) {
        execute(premiumSnapshot, eventPublisher);
    }

    @Override
    public int orderComparator(Operation operation) {
        return Stream.of(register, end)
                .max(LocalDate::compareTo)
                .map(maxDate -> maxDate.compareTo(operation.getDate()))
                .orElseThrow();
    }

    @Override
    protected void publishFailedEvent(Exception e, DomainEventPublisher eventPublisher) {
        // todo: zaimplementować
    }

}