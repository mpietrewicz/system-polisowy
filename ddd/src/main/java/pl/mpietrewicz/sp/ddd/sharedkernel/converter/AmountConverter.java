package pl.mpietrewicz.sp.ddd.sharedkernel.converter;

import pl.mpietrewicz.sp.ddd.sharedkernel.exception.NotPositiveAmountException;
import pl.mpietrewicz.sp.ddd.sharedkernel.valueobject.Amount;
import pl.mpietrewicz.sp.ddd.sharedkernel.valueobject.PositiveAmount;
import pl.mpietrewicz.sp.ddd.sharedkernel.valueobject.ZeroAmount;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;
import java.math.BigDecimal;

@Converter(autoApply = true)
public class AmountConverter implements AttributeConverter<Amount, BigDecimal> {

    @Override
    public BigDecimal convertToDatabaseColumn(Amount attribute) {
        if (attribute == null) {
            return null;
        }
        return attribute.getValue();
    }

    @Override
    public Amount convertToEntityAttribute(BigDecimal dbData) {
        if (dbData == null) {
            return null;
        }

        try {
            return PositiveAmount.withValue(dbData);
        } catch (NotPositiveAmountException e) {
            if (dbData.signum() == 0) {
                return new ZeroAmount();
            } else {
                return null;
            }
        }
    }

}