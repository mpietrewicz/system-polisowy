package pl.mpietrewicz.sp.modules.contract.domain.premium;


import pl.mpietrewicz.sp.ddd.annotations.domain.DomainFactory;
import pl.mpietrewicz.sp.ddd.canonicalmodel.publishedlanguage.AggregateId;
import pl.mpietrewicz.sp.ddd.canonicalmodel.publishedlanguage.snapshot.ComponentData;
import pl.mpietrewicz.sp.ddd.sharedkernel.Amount;

import java.time.LocalDate;

@DomainFactory
public class PremiumFactory {

    public Premium create(ComponentData componentData, LocalDate since, Amount amount) {
        AggregateId aggregateId = AggregateId.generate();
        PremiumHistory premiumHistory = new PremiumHistory(since, amount);
        return new Premium(aggregateId, componentData, premiumHistory);
    }

}