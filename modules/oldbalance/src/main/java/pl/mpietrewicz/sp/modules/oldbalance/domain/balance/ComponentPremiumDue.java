package pl.mpietrewicz.sp.modules.oldbalance.domain.balance;

import pl.mpietrewicz.sp.ddd.annotations.domain.ValueObject;
import pl.mpietrewicz.sp.ddd.support.domain.BaseEntity;
import pl.mpietrewicz.sp.ddd.canonicalmodel.publishedlanguage.snapshot.ComponentData;

import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import java.math.BigDecimal;

@ValueObject
@Entity
public class ComponentPremiumDue extends BaseEntity {

    @Embedded
    private ComponentData componentData;

    private BigDecimal premiumDue;

    @Enumerated(EnumType.STRING)
    private DueStatus dueStatus = DueStatus.CURRENT;

    public ComponentPremiumDue() {
    }

    public ComponentPremiumDue(ComponentData componentData, BigDecimal premiumDue) {
        this.componentData = componentData;
        this.premiumDue = premiumDue;
    }

    protected ComponentData getComponentData() {
        return componentData;
    }

    protected BigDecimal getPremiumDue() {
        return premiumDue;
    }

    protected void changePremiumDue(BigDecimal amount) {
        this.premiumDue = amount;
    }

}