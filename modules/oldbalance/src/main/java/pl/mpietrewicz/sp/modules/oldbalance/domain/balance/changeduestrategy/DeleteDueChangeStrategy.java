package pl.mpietrewicz.sp.modules.oldbalance.domain.balance.changeduestrategy;

import lombok.RequiredArgsConstructor;
import pl.mpietrewicz.sp.modules.oldbalance.domain.balance.PremiumDue;
import pl.mpietrewicz.sp.modules.oldbalance.domain.balance.DueChangeStrategy;
import pl.mpietrewicz.sp.ddd.canonicalmodel.publishedlanguage.snapshot.ComponentData;

@RequiredArgsConstructor
public class DeleteDueChangeStrategy implements DueChangeStrategy {

    private final ComponentData componentData;

    @Override
    public void execute(PremiumDue premiumDue) {
        premiumDue.delete(componentData);
    }

}