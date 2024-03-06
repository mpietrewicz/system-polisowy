package pl.mpietrewicz.sp.modules.balance.infrastructure.repo.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import pl.mpietrewicz.sp.ddd.support.infrastructure.repo.BaseEntity;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.time.LocalDate;
import java.util.List;

@Table(name = "Period")
@Entity
@Getter
@NoArgsConstructor
public class PeriodEntity extends BaseEntity {

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "period_id")
    public List<MonthEntity> months;

    private LocalDate start;

    private boolean isValid;

    public PeriodEntity(Long entityId, List<MonthEntity> months, LocalDate start, boolean isValid) {
        this.entityId = entityId;
        this.months = months;
        this.start = start;
        this.isValid = isValid;
    }

}