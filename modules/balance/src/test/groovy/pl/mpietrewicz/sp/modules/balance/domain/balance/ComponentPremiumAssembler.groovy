package pl.mpietrewicz.sp.modules.balance.domain.balance

import pl.mpietrewicz.sp.ddd.canonicalmodel.publishedlanguage.AggregateId
import pl.mpietrewicz.sp.ddd.sharedkernel.Amount
import pl.mpietrewicz.sp.modules.balance.domain.balance.month.ComponentPremium

class ComponentPremiumAssembler {

    private AggregateId componentId = AggregateId.generate();

    private Amount amount;


    ComponentPremiumAssembler builder() {
        this
    }

    ComponentPremiumAssembler withComponentId(String componentId) {
        this.componentId = new AggregateId(componentId)
        this
    }

    ComponentPremiumAssembler withAmount(String amount) {
        this.amount = new Amount(amount)
        this
    }

    ComponentPremium build() {
        new ComponentPremium(componentId, amount)
    }

}