package pl.mpietrewicz.sp.modules.oldbalance.domain.utils;

import pl.mpietrewicz.sp.modules.oldbalance.domain.balance.ComponentPremiumDue;
import pl.mpietrewicz.sp.ddd.canonicalmodel.publishedlanguage.snapshot.ComponentData;

import java.math.BigDecimal;

public class ComponentDueAssembler {

    private ComponentData componentData;

    private BigDecimal premiumDue;

    public ComponentDueAssembler withComponentData(ComponentData componentData) {
        this.componentData = componentData;
        return this;
    }

    public ComponentDueAssembler withPremiumDue(int premiumDue) {
        this.premiumDue = new BigDecimal(premiumDue);
        return this;
    }

    public ComponentPremiumDue build() {
        return new ComponentPremiumDue(componentData, premiumDue);
    }
}