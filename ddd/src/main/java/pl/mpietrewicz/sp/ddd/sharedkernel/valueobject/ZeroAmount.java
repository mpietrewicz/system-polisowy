package pl.mpietrewicz.sp.ddd.sharedkernel.valueobject;

import pl.mpietrewicz.sp.ddd.annotations.domain.ValueObject;

import javax.persistence.Embeddable;
import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;

@ValueObject
@Embeddable
public class ZeroAmount implements Amount, Serializable {

    private final BigDecimal value = BigDecimal.ZERO;

    public ZeroAmount() {}

    @Override
    public BigDecimal getValue() {
        return value;
    }

    @Override
    public boolean isPositive() {
        return false;
    }

    @Override
    public String toString() {
        return value.setScale(2, RoundingMode.HALF_UP).toString();
    }

}