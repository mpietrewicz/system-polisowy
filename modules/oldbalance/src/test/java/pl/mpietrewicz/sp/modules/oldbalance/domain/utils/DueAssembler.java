package pl.mpietrewicz.sp.modules.oldbalance.domain.utils;

import pl.mpietrewicz.sp.modules.oldbalance.domain.balance.ComponentPremiumDue;
import pl.mpietrewicz.sp.modules.oldbalance.domain.balance.PremiumDue;

import java.util.ArrayList;
import java.util.List;

public class DueAssembler {

    private List<ComponentPremiumDue> componentPremiumDues = new ArrayList<>();

    public DueAssembler withComponentDues(List<ComponentPremiumDue> componentPremiumDues) {
        this.componentPremiumDues = componentPremiumDues;
        return this;
    }

    public PremiumDue build() {
        return new PremiumDue(componentPremiumDues);
    }
}