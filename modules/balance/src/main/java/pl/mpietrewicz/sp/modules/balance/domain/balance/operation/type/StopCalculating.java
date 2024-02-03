package pl.mpietrewicz.sp.modules.balance.domain.balance.operation.type;

import lombok.NoArgsConstructor;
import pl.mpietrewicz.sp.ddd.annotations.domain.ValueObject;
import pl.mpietrewicz.sp.ddd.canonicalmodel.publishedlanguage.snapshot.premium.PremiumSnapshot;
import pl.mpietrewicz.sp.ddd.support.domain.DomainEventPublisher;
import pl.mpietrewicz.sp.modules.balance.domain.balance.operation.Operation;
import pl.mpietrewicz.sp.modules.balance.exceptions.ReexecutionException;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import java.time.YearMonth;

import static pl.mpietrewicz.sp.modules.balance.domain.balance.operation.OperationType.STOP_CALCULATING;

@ValueObject
@Entity
@DiscriminatorValue("STOP_CALCULATING")
@NoArgsConstructor
public class StopCalculating extends Operation {

    public StopCalculating(YearMonth from) {
        super(from.atDay(1));
        this.type = STOP_CALCULATING;
    }

    public void execute(Operation previousOperation, PremiumSnapshot premiumSnapshot, DomainEventPublisher eventPublisher) {
        this.periods = null;
    }

    @Override
    public void execute(PremiumSnapshot premiumSnapshot, DomainEventPublisher eventPublisher) {
    }

    @Override
    protected void reexecute(PremiumSnapshot premiumSnapshot, DomainEventPublisher eventPublisher) {
    }

    @Override
    public int orderComparator(Operation operation) {
        return 1;
    }

    @Override
    public void handle(ReexecutionException e, DomainEventPublisher eventPublisher) {

    }

}