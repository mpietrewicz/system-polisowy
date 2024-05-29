package pl.mpietrewicz.sp.ddd.sharedkernel.valueobject;

import pl.mpietrewicz.sp.ddd.annotations.domain.ValueObject;
import pl.mpietrewicz.sp.ddd.sharedkernel.exception.NotPositiveAmountException;

import javax.persistence.Embeddable;
import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;

@ValueObject
@Embeddable
public class PositiveAmount implements Amount, Serializable {

    public static final PositiveAmount TEN = new PositiveAmount(BigDecimal.TEN);

    private BigDecimal value;

    private PositiveAmount() {
    }

    private PositiveAmount(BigDecimal value) {
        this.value = value;
    }

    public static PositiveAmount withValue(BigDecimal value) throws NotPositiveAmountException {
        if (value.signum() > 0) {
            return new PositiveAmount(value);
        } else {
            throw new NotPositiveAmountException(value);
        }
    }

    public static PositiveAmount withValue(String val) throws NotPositiveAmountException {
        return withValue(new BigDecimal(val));
    }

    @Override
    public BigDecimal getValue() {
        return value;
    }

    @Override
    public boolean isPositive() {
        return true;
    }

    public boolean equals(PositiveAmount positiveAmount) {
        return this.value.compareTo(positiveAmount.value) == 0;
    }

    public boolean equals(ZeroAmount zeroAmount) {
        return false;
    }

    public boolean isLessThan(PositiveAmount positiveAmount) {
        return this.value.compareTo(positiveAmount.value) < 0;
    }

    public boolean isHigherThan(PositiveAmount positiveAmount) {
        return this.value.compareTo(positiveAmount.value) > 0;
    }

    public PositiveAmount subtract(PositiveAmount positiveAmount) {
        BigDecimal subtractionResult = this.value.subtract(positiveAmount.value);
        if (subtractionResult.signum() > 0) {
            return new PositiveAmount(subtractionResult);
        } else {
            throw new IllegalStateException("Wynik odejmowania kwot nie może być mniejszy lub równy zero");
        }
    }

    public PositiveAmount add(PositiveAmount positiveAmount) {
        return new PositiveAmount(this.value.add(positiveAmount.value));
    }

    @Override
    public String toString() {
        return value.setScale(2, RoundingMode.HALF_UP).toString();
    }

    public PositiveAmount negate() {
        return new PositiveAmount(value.negate());
    }

    protected BigDecimal getBigDecimal() {
        return value;
    }

}