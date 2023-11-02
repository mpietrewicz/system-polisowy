package pl.mpietrewicz.sp.modules.contract.domain.component;

import lombok.Setter;
import pl.mpietrewicz.sp.ddd.annotations.domain.ValueObject;
import pl.mpietrewicz.sp.modules.contract.ddd.support.domain.BaseEntity;

import javax.persistence.Entity;
import java.time.LocalDate;

@ValueObject
@Entity
public class LiabilityPeriod extends BaseEntity {

    private LocalDate start;
    @Setter
    private LocalDate end;

    public LiabilityPeriod() {
    }

    public LiabilityPeriod(LocalDate start) {
        this.start = start;
    }

    public boolean isAt(LocalDate date) {
        return start.compareTo(date) <= 0
                && (end == null || end.compareTo(date) >= 0);
    }
}