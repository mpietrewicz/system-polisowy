package pl.mpietrewicz.sp.modules.balance.infrastructure.repo.converter;

import org.springframework.stereotype.Component;
import pl.mpietrewicz.sp.modules.balance.domain.balance.Period;
import pl.mpietrewicz.sp.modules.balance.domain.balance.month.Month;
import pl.mpietrewicz.sp.modules.balance.infrastructure.repo.entity.MonthEntity;
import pl.mpietrewicz.sp.modules.balance.infrastructure.repo.entity.PeriodEntity;

import javax.inject.Inject;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class PeriodConverter {

    @Inject
    private MonthConverter monthConverter;

    public Period convert(PeriodEntity entity) {
        List<Month> months = entity.getMonths().stream()
                .map(e -> monthConverter.convert(e))
                .collect(Collectors.toList());
        return new Period(entity.getEntityId(), entity.getStart(), months, entity.isValid());
    }

    public PeriodEntity convert(Period model) {
        List<MonthEntity> months = model.getMonths().stream()
                .map(m -> monthConverter.convert(m))
                .collect(Collectors.toList());

        return new PeriodEntity(model.getId(), months, model.getStart(), model.isValid());
    }

}