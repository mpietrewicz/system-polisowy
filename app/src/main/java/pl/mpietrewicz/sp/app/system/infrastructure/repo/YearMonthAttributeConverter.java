package pl.mpietrewicz.sp.app.system.infrastructure.repo;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;
import java.sql.Date;
import java.time.YearMonth;

@Converter(autoApply = true)
public class YearMonthAttributeConverter implements AttributeConverter<YearMonth, Date> {

    @Override
    public Date convertToDatabaseColumn(YearMonth attribute) {
        return Date.valueOf(attribute.atDay(1));
    }

    @Override
    public YearMonth convertToEntityAttribute(Date dbData) {
        return YearMonth.from(dbData.toLocalDate());
    }

}