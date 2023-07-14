package pl.mpietrewicz.sp.modules.oldbalance.domain.utils;

import pl.mpietrewicz.sp.ddd.canonicalmodel.publishedlanguage.AggregateId;
import pl.mpietrewicz.sp.ddd.canonicalmodel.publishedlanguage.Frequency;
import pl.mpietrewicz.sp.ddd.canonicalmodel.publishedlanguage.snapshot.ContractData;
import pl.mpietrewicz.sp.modules.contract.domain.component.Component;
import pl.mpietrewicz.sp.modules.contract.domain.component.ComponentType;
import pl.mpietrewicz.sp.modules.contract.domain.component.Liability;
import pl.mpietrewicz.sp.modules.contract.domain.component.LiabilityPeriod;

import java.time.LocalDate;

public class ComponentAssembler {

    private ContractData contractData = new ContractData(AggregateId.generate(), LocalDate.parse("2022-05-01"), Frequency.QUARTERLY);
    private LocalDate startDate;
    private ComponentType componentType;

    public ComponentAssembler withContractData(ContractData contractData) {
        this.contractData = contractData;
        return this;
    }

    public ComponentAssembler withStartDate(LocalDate startDate) {
        this.startDate = startDate;
        return this;
    }

    public ComponentAssembler withComponentType(ComponentType componentType) {
        this.componentType = componentType;
        return this;
    }

    public Component build() {
        LiabilityPeriod liabilityPeriod = new LiabilityPeriod(startDate);
        Liability liability = new Liability(liabilityPeriod);
        return new Component(AggregateId.generate(), contractData, startDate, componentType, liability);
    }
}