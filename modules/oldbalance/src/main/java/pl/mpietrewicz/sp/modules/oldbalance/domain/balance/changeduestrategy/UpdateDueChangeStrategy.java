package pl.mpietrewicz.sp.modules.oldbalance.domain.balance.changeduestrategy;

import lombok.RequiredArgsConstructor;
import pl.mpietrewicz.sp.modules.oldbalance.domain.balance.PremiumDue;
import pl.mpietrewicz.sp.modules.oldbalance.domain.balance.DueChangeStrategy;
import pl.mpietrewicz.sp.ddd.canonicalmodel.publishedlanguage.snapshot.ComponentData;

import java.math.BigDecimal;

@RequiredArgsConstructor
public class UpdateDueChangeStrategy implements DueChangeStrategy {

    private final ComponentData componentData;
    private final BigDecimal premium;

    @Override
    public void execute(PremiumDue premiumDue) {
        premiumDue.update(componentData, premium);
    }

}