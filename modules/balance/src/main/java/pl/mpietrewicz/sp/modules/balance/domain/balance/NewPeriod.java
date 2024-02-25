package pl.mpietrewicz.sp.modules.balance.domain.balance;

import lombok.NoArgsConstructor;
import pl.mpietrewicz.sp.ddd.support.infrastructure.repo.BaseEntity;
import pl.mpietrewicz.sp.modules.balance.domain.balance.state.NewMonth;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;

@Entity
@NoArgsConstructor
public class NewPeriod extends BaseEntity {

    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "new_period_id")
    private final List<NewMonth> newMonths = new ArrayList<>();

    public void addNewMonth(NewMonth newMonth) {
        newMonths.add(newMonth);
    }

    public List<NewMonth> getNewMonths() {
        return newMonths;
    }

}