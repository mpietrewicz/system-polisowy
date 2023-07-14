package pl.mpietrewicz.sp.modules.oldbalance.domain.balance;

import pl.mpietrewicz.sp.ddd.annotations.domain.ValueObject;
import pl.mpietrewicz.sp.ddd.canonicalmodel.publishedlanguage.snapshot.ComponentData;
import pl.mpietrewicz.sp.ddd.support.domain.BaseEntity;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@ValueObject
@Entity
public class PremiumDue extends BaseEntity {

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    @JoinColumn(name = "premium_due_id")
    private List<ComponentPremiumDue> componentPremiumDues = new ArrayList<>();;

    public PremiumDue() {
    }

    public PremiumDue(List<ComponentPremiumDue> componentPremiumDues) {
        this.componentPremiumDues.addAll(componentPremiumDues);
    }

    public BigDecimal getPremiumDue() {
        return componentPremiumDues.stream()
                .map(ComponentPremiumDue::getPremiumDue)
                .reduce(BigDecimal::add)
                .orElse(BigDecimal.ZERO);
    }

    public void update(ComponentData componentData, BigDecimal newPremium) { // todo: zamienić na Premium
        ComponentPremiumDue componentPremiumDue = getComponentDue(componentData);
        BigDecimal actualPremium = componentPremiumDue.getPremiumDue();
        componentPremiumDue.changePremiumDue(newPremium);
    }

    public void add(ComponentData componentData, BigDecimal premium) {
        ComponentPremiumDue componentPremiumDue = new ComponentPremiumDue(componentData, premium);
        this.componentPremiumDues.add(componentPremiumDue);
    }

    public void delete(ComponentData componentData) {
        ComponentPremiumDue componentPremiumDue = getComponentDue(componentData);
        this.componentPremiumDues.remove(componentPremiumDue);
    }

    private ComponentPremiumDue getComponentDue(ComponentData componentData) {
        return this.componentPremiumDues.stream()
                .filter(cd -> cd.getComponentData().equals(componentData))
                .findAny()
                .orElseThrow();
    }

}