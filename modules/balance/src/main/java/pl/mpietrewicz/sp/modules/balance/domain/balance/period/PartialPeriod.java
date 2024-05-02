package pl.mpietrewicz.sp.modules.balance.domain.balance.period;

import lombok.Getter;
import lombok.NoArgsConstructor;
import pl.mpietrewicz.sp.ddd.support.infrastructure.repo.BaseEntity;
import pl.mpietrewicz.sp.modules.balance.domain.balance.month.Month;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import java.time.LocalDate;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor
public class PartialPeriod extends BaseEntity {

    private LocalDate start;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "partial_period_id")
    private List<Month> months;

    private boolean isValid;

    private String info;

    public PartialPeriod(LocalDate start, List<Month> months, boolean isValid, String info) {
        this.start = start;
        this.months = months;
        this.isValid = isValid;
        this.info = info;
    }

    public void markAsInvalid() {
        this.isValid = false;
    }

    public boolean isValid() {
        return isValid;
    }

}