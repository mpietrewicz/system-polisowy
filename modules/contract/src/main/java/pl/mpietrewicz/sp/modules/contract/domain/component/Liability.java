package pl.mpietrewicz.sp.modules.contract.domain.component;

import pl.mpietrewicz.sp.ddd.annotations.domain.ValueObject;
import pl.mpietrewicz.sp.ddd.support.domain.BaseEntity;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@ValueObject
@Entity
public class Liability extends BaseEntity {

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "liability_id")
    List<LiabilityPeriod> liabilityPeriods = new ArrayList<>();

    public Liability() {
    }

    public Liability(LiabilityPeriod liabilityPeriods) {
        this.liabilityPeriods.add(liabilityPeriods);
    }

    public void end(LocalDate date) {
        LiabilityPeriod liabilityPeriod = liabilityPeriods.stream()
                .filter(period -> period.isAt(date))
                .findAny()
                .orElseThrow(() -> new RuntimeException("You're trying to end not existing liability!"));
        liabilityPeriod.setEnd(date);
    }

}