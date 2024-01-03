package pl.mpietrewicz.sp.modules.balance.domain.balance.operation.type;

import lombok.NoArgsConstructor;
import pl.mpietrewicz.sp.ddd.annotations.domain.ValueObject;
import pl.mpietrewicz.sp.modules.balance.domain.balance.paymentpolicy.Period;
import pl.mpietrewicz.sp.modules.balance.domain.balance.Premium;
import pl.mpietrewicz.sp.modules.balance.domain.balance.month.ComponentPremium;
import pl.mpietrewicz.sp.modules.balance.domain.balance.operation.Operation;

import javax.persistence.CascadeType;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.OneToOne;
import java.time.YearMonth;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static pl.mpietrewicz.sp.modules.balance.domain.balance.operation.OperationType.START_CALCULATING;

@ValueObject
@Entity
@DiscriminatorValue("START_CONTRACT")
@NoArgsConstructor
public class StartCalculating extends Operation {

    @OneToOne(cascade = CascadeType.ALL)
    private ComponentPremium componentPremium; // todo: zamienić na listę ComponentPremium

    public StartCalculating(YearMonth from, ComponentPremium componentPremium) {
        super(from.atDay(1));
        this.componentPremium = componentPremium;
        this.type = START_CALCULATING;
        this.pending = false;
    }

    public void execute(int grace) {
        this.premium = new Premium(Stream.of(componentPremium).collect(Collectors.toList()));
        this.period = Period.init(date, premium, grace);
        this.pending = false;
    }

    @Override
    public void execute() {
        throw new UnsupportedOperationException("Metoda nie obsługiwana w StartCalculating Operation");
    }

    @Override
    public int orderComparator(Operation operation) {
        return -1;
    }

}