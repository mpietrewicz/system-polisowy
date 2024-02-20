package pl.mpietrewicz.sp.modules.contract.domain.premium;


import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import pl.mpietrewicz.sp.ddd.annotations.domain.DomainFactory;
import pl.mpietrewicz.sp.ddd.canonicalmodel.publishedlanguage.AggregateId;
import pl.mpietrewicz.sp.ddd.canonicalmodel.publishedlanguage.snapshot.ComponentData;
import pl.mpietrewicz.sp.ddd.canonicalmodel.publishedlanguage.snapshot.ContractData;
import pl.mpietrewicz.sp.ddd.sharedkernel.Amount;

import javax.inject.Inject;
import java.time.LocalDateTime;

@DomainFactory
public class PremiumFactory {

    @Inject
    private AutowireCapableBeanFactory spring;

    public Premium create(ContractData contractData, ComponentData basicComponentData, Amount premiumAmount) {
        AggregateId aggregateId = AggregateId.generate();
        ComponentPremium basicComponentPremium = new ComponentPremium(basicComponentData);
        basicComponentPremium.addPremium(basicComponentData.getStartDate(), premiumAmount, LocalDateTime.now());
        Premium premium = new Premium(aggregateId, contractData, basicComponentPremium);
        spring.autowireBean(premium);

        return premium;
    }

}