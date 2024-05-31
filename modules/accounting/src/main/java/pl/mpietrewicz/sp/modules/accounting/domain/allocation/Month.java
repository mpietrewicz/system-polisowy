package pl.mpietrewicz.sp.modules.accounting.domain.allocation;

import lombok.Getter;
import lombok.NoArgsConstructor;
import pl.mpietrewicz.sp.ddd.annotations.domain.ValueObject;
import pl.mpietrewicz.sp.ddd.support.infrastructure.repo.BaseEntity;

import javax.persistence.Entity;
import javax.persistence.OneToMany;
import java.time.YearMonth;
import java.util.List;

import static javax.persistence.CascadeType.ALL;

@ValueObject
@Entity
@NoArgsConstructor
public class Month extends BaseEntity {

    @Getter
    private YearMonth yearMonth;

    @OneToMany(cascade = ALL)
    private List<Risk> risks;

    public Month(YearMonth yearMonth, List<Risk> risks) {
        this.yearMonth = yearMonth;
        this.risks = risks;
    }

}