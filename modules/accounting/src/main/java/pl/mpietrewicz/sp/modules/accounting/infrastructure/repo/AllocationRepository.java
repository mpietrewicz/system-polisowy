package pl.mpietrewicz.sp.modules.accounting.infrastructure.repo;

import pl.mpietrewicz.sp.ddd.annotations.domain.DomainRepository;
import pl.mpietrewicz.sp.ddd.canonicalmodel.publishedlanguage.AggregateId;
import pl.mpietrewicz.sp.ddd.canonicalmodel.publishedlanguage.RiskDefinition;
import pl.mpietrewicz.sp.modules.accounting.domain.allocation.Allocation;

import java.util.List;
import java.util.Optional;

@DomainRepository
public interface AllocationRepository {

    Allocation load(AggregateId allocationId);

    void save(Allocation allocation);

    Optional<Allocation> findByContractId(AggregateId contractId);

    List<RiskDefinition> findRiskDefinitions(AggregateId componentId);

}