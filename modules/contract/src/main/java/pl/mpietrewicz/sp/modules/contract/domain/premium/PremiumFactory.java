package pl.mpietrewicz.sp.modules.contract.domain.premium;


import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import pl.mpietrewicz.sp.ddd.annotations.domain.DomainFactory;
import pl.mpietrewicz.sp.ddd.canonicalmodel.publishedlanguage.AggregateId;
import pl.mpietrewicz.sp.ddd.canonicalmodel.publishedlanguage.snapshot.ComponentData;
import pl.mpietrewicz.sp.ddd.canonicalmodel.publishedlanguage.snapshot.ContractData;
import pl.mpietrewicz.sp.ddd.sharedkernel.valueobject.PositiveAmount;
import pl.mpietrewicz.sp.modules.contract.domain.premium.component.BasicComponentPremium;
import pl.mpietrewicz.sp.modules.contract.domain.premium.operation.AddPremium;

import javax.inject.Inject;
import java.time.LocalDate;
import java.time.LocalDateTime;

import static pl.mpietrewicz.sp.modules.contract.domain.premium.ChangePremiumPolicyEnum.EVERYTIME;

@DomainFactory
public class PremiumFactory {

    @Inject
    private AutowireCapableBeanFactory spring;

    public Premium create(ContractData contractData, ComponentData basicComponentData, PositiveAmount premiumPositiveAmount) {
        LocalDate componentStart = basicComponentData.getStartDate();
        LocalDateTime now = LocalDateTime.now();
        AggregateId componentId = basicComponentData.getAggregateId();

        AddPremium addPremium = new AddPremium(componentStart, premiumPositiveAmount, now);
        BasicComponentPremium basicComponentPremium = new BasicComponentPremium(componentId, addPremium, EVERYTIME);
        Premium premium = new Premium(AggregateId.generate(), contractData.getAggregateId(), basicComponentPremium);
        spring.autowireBean(premium);

        return premium;
    }

}