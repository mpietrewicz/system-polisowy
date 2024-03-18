package pl.mpietrewicz.sp.modules.contract.domain.premium.operation;

import lombok.NoArgsConstructor;
import pl.mpietrewicz.sp.ddd.annotations.domain.ValueObject;
import pl.mpietrewicz.sp.ddd.sharedkernel.valueobject.Amount;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import java.time.LocalDate;
import java.time.LocalDateTime;

@ValueObject
@Entity
@DiscriminatorValue("CHANGE")
@NoArgsConstructor
public class ChangePremium extends Operation {

    public ChangePremium(LocalDate date, Amount amount, LocalDateTime timestamp) {
        super(date, amount, timestamp);
    }

    @Override
    public Type getType() {
        return Type.CHANGE;
    }

}