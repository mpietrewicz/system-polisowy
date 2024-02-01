package pl.mpietrewicz.sp.modules.balance.domain.balance.operation.type;

import lombok.NoArgsConstructor;
import pl.mpietrewicz.sp.ddd.annotations.domain.ValueObject;
import pl.mpietrewicz.sp.ddd.canonicalmodel.publishedlanguage.snapshot.premium.PremiumSnapshot;
import pl.mpietrewicz.sp.ddd.sharedkernel.Amount;
import pl.mpietrewicz.sp.modules.balance.domain.balance.operation.Operation;
import pl.mpietrewicz.sp.modules.balance.domain.balance.Period;

import javax.persistence.AttributeOverride;
import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import java.time.YearMonth;
import java.util.ArrayList;

import static pl.mpietrewicz.sp.modules.balance.domain.balance.operation.OperationType.START_CALCULATING;

@ValueObject
@Entity
@DiscriminatorValue("START_CONTRACT")
@NoArgsConstructor
public class StartCalculating extends Operation {

    @Embedded
    @AttributeOverride(name = "value", column = @Column(name = "amount"))
    private Amount premium;

    public StartCalculating(YearMonth start, Amount premium) {
        super(start.atDay(1));
        this.type = START_CALCULATING;
        this.premium = premium;
        this.pending = false;
    }

    public void execute() {
        this.period = new Period(date, new ArrayList<>());
        this.pending = false;
    }

    @Override
    public int orderComparator(Operation operation) {
        return -1;
    }

    @Override
    protected void execute(PremiumSnapshot premiumSnapshot) {
        throw new UnsupportedOperationException("Metoda nie obsługiwana w StartCalculating Operation");
    }

}