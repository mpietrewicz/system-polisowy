package pl.mpietrewicz.sp.ddd.sharedkernel;

import pl.mpietrewicz.sp.ddd.annotations.domain.ValueObject;

import javax.persistence.Embeddable;
import java.math.BigDecimal;

@ValueObject
@Embeddable
public class PositiveAmount extends Amount {

    public static final PositiveAmount ZERO = new PositiveAmount(BigDecimal.ZERO);
    public static final PositiveAmount TEN = new PositiveAmount(BigDecimal.TEN);

    public PositiveAmount() {
        super();
    }

    public PositiveAmount(String value) {
        super(value);
    }

    public PositiveAmount(BigDecimal value) {
        super(value);
    }

    public boolean isPositive() {
        return true;
    }

    @Override
    public String toString() {
        return getBigDecimal().toString();
    }

    public Amount getAmount() {
        return new Amount(value);
    }

    public PositiveAmount add(Amount amount) {
        return new PositiveAmount(super.value.add(amount.value));
    }

    public PositiveAmount add(PositiveAmount amount) {
        return new PositiveAmount(super.value.add(amount.value));
    }

}