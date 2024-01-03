package pl.mpietrewicz.sp.ddd.sharedkernel;

import pl.mpietrewicz.sp.ddd.annotations.domain.ValueObject;

import javax.persistence.Embeddable;
import java.math.BigDecimal;

@ValueObject
@Embeddable
public class PositiveAmount extends Amount {

    public PositiveAmount() {
        super();
    }

    public PositiveAmount(BigDecimal value) {
        super(value);
    }

    public boolean isPositive() {
        return true;
    }

}