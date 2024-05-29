package pl.mpietrewicz.sp.modules.accounting.domain.allocation;

import pl.mpietrewicz.sp.ddd.annotations.domain.DomainFactory;
import pl.mpietrewicz.sp.ddd.canonicalmodel.publishedlanguage.AggregateId;

@DomainFactory
public class AllocationFactory {

    public Allocation create(AggregateId contractId) {
        return new Allocation(AggregateId.generate(), contractId);
    }

}