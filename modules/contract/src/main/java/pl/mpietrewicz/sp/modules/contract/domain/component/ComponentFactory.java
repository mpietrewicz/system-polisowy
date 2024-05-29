package pl.mpietrewicz.sp.modules.contract.domain.component;

import pl.mpietrewicz.sp.ddd.annotations.domain.DomainFactory;
import pl.mpietrewicz.sp.ddd.canonicalmodel.publishedlanguage.AggregateId;
import pl.mpietrewicz.sp.ddd.canonicalmodel.publishedlanguage.snapshot.ContractData;

import java.time.LocalDate;

@DomainFactory
public class ComponentFactory {

    public Component createBasicComponent(ContractData contractData, String name) {
        AggregateId aggregateId = AggregateId.generate();
        AggregateId contractId = contractData.getAggregateId();
        LocalDate startDate = contractData.getStart();

        return new Component(aggregateId, contractId, name, startDate, ComponentType.BASIC);
    }

    public Component createAdditionalComponent(ContractData contractData, String name, LocalDate registerDate) {
        AggregateId aggregateId = AggregateId.generate();
        AggregateId contractId = contractData.getAggregateId();
        LocalDate startDate = LocalDate.from(registerDate);

        return new Component(aggregateId, contractId, name, startDate, ComponentType.ADDITIONAL);
    }
}