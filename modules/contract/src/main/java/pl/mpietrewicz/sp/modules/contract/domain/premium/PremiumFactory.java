package pl.mpietrewicz.sp.modules.contract.domain.premium;


import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import pl.mpietrewicz.sp.ddd.annotations.domain.DomainFactory;
import pl.mpietrewicz.sp.ddd.canonicalmodel.publishedlanguage.AggregateId;
import pl.mpietrewicz.sp.ddd.canonicalmodel.publishedlanguage.snapshot.ComponentData;
import pl.mpietrewicz.sp.ddd.canonicalmodel.publishedlanguage.snapshot.ContractData;
import pl.mpietrewicz.sp.ddd.sharedkernel.Amount;

import javax.inject.Inject;
import java.time.LocalDate;

@DomainFactory
public class PremiumFactory {

    @Inject
    private AutowireCapableBeanFactory spring;

    public Premium create(ContractData contractData, ComponentData componentData, Amount premiumAmount) {
        AggregateId aggregateId = AggregateId.generate();
        Premium premium = new Premium(aggregateId, contractData);
        spring.autowireBean(premium);
        LocalDate componentStart = componentData.getStartDate();
        premium.add(componentData, componentStart, premiumAmount);

        return premium;
    }

}