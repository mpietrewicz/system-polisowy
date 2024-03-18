package pl.mpietrewicz.sp.ddd.sharedkernel.valueobject;

import pl.mpietrewicz.sp.ddd.annotations.domain.ValueObject;

import javax.persistence.Embeddable;
import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;

@ValueObject
@Embeddable
public class Amount implements Serializable {

    public static final Amount ZERO = new Amount(BigDecimal.ZERO);
    public static final Amount TEN = new Amount(BigDecimal.TEN);

    public BigDecimal value;

    public Amount() {
    }

    public Amount(BigDecimal value) {
        this.value = value;
    }

    public Amount(String val) {
        this.value = new BigDecimal(val);
    }

    public boolean equals(Amount amount) {
        return this.value.compareTo(amount.value) == 0;
    }

    public boolean isLessThan(Amount amount) {
        return this.value.compareTo(amount.value) < 0;
    }

    public boolean isHigherThan(Amount amount) {
        return this.value.compareTo(amount.value) > 0;
    }

    public boolean isPositive() {
        return this.isHigherThan(ZERO);
    }

    public PositiveAmount castToPositive() {
        if (!isPositive()) throw new IllegalStateException("kwota nie jest dodatnia!");
        return new PositiveAmount(value);
    }

    public Amount subtract(Amount amount) {
        BigDecimal subtractionResult = this.value.subtract(amount.value);
        if (subtractionResult.signum() >= 0) {
            return new Amount(subtractionResult);
        } else {
            throw new IllegalStateException("Wynik odejmowania kwot nie może być mniejszy od zera");
        }
    }

    public Amount add(Amount amount) {
        return new Amount(this.value.add(amount.value));
    }

    @Override
    public String toString() {
        return value.setScale(2, RoundingMode.HALF_UP).toString();
    }

    public Amount negate() {
        return new Amount(value.negate());
    }

    public Amount absolute() {
        return new Amount(value.abs());
    }

    protected BigDecimal getBigDecimal() {
        return value;
    }

}