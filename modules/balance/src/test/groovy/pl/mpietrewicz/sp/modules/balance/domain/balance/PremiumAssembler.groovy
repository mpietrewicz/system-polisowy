package pl.mpietrewicz.sp.modules.balance.domain.balance

import pl.mpietrewicz.sp.modules.balance.domain.balance.month.ComponentPremium

class PremiumAssembler {

    private List<ComponentPremium> componentPremiums = new ArrayList<>();

    PremiumAssembler builder() {
        this
    }

    PremiumAssembler addComponentPremium(ComponentPremium componentPremium) {
        this.componentPremiums.add(componentPremium)
        this
    }

    Premium build() {
        new Premium(componentPremiums)
    }

}