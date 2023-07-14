package pl.mpietrewicz.sp.modules.contract.domain.component;

import pl.mpietrewicz.sp.ddd.annotations.domain.DomainFactory;
import pl.mpietrewicz.sp.DateUtils;
import pl.mpietrewicz.sp.ddd.canonicalmodel.publishedlanguage.AggregateId;
import pl.mpietrewicz.sp.ddd.canonicalmodel.publishedlanguage.snapshot.ContractData;

import java.time.LocalDate;

@DomainFactory
public class ComponentFactory {

    public Component createBasicComponent(ContractData contractData) {
        AggregateId aggregateId = AggregateId.generate();
        LocalDate startDate = contractData.getContractStartDate();
        LiabilityPeriod liabilityPeriod = new LiabilityPeriod(startDate);
        Liability liability = new Liability(liabilityPeriod);
        return new Component(aggregateId, contractData, startDate, ComponentType.BASIC, liability);
    }

    public Component createAdditionalComponent(ContractData contractData, LocalDate registerDate) {
        AggregateId aggregateId = AggregateId.generate();
        LocalDate startDate = DateUtils.pierwszyDzienMiesiaca(registerDate);
        LiabilityPeriod liabilityPeriod = new LiabilityPeriod(startDate);
        Liability liability = new Liability(liabilityPeriod);

        return new Component(aggregateId, contractData, startDate, ComponentType.ADDITIONAL, liability);
    }
}