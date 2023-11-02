package pl.mpietrewicz.sp.modules.accounting.infrastructure.repo.impl;

import pl.mpietrewicz.sp.ddd.annotations.domain.DomainRepositoryImpl;
import pl.mpietrewicz.sp.ddd.canonicalmodel.publishedlanguage.AggregateId;
import pl.mpietrewicz.sp.ddd.canonicalmodel.publishedlanguage.RiskDefinition;
import pl.mpietrewicz.sp.ddd.sharedkernel.Divisor;
import pl.mpietrewicz.sp.modules.accounting.ddd.support.infrastructure.repository.jpa.GenericJpaRepository;
import pl.mpietrewicz.sp.modules.accounting.domain.allocation.Allocation;
import pl.mpietrewicz.sp.modules.accounting.infrastructure.repo.AllocationRepository;

import java.util.List;

@DomainRepositoryImpl
public class JpaAllocationRepository extends GenericJpaRepository<Allocation> implements AllocationRepository {

    @Override
    public Allocation findByContractId(AggregateId contractId) {
        String query = "SELECT a FROM Allocation a WHERE a.contractData.aggregateId = :contractId";
        return entityManager.createQuery(query, Allocation.class)
                .setParameter("contractId", contractId)
                .getSingleResult();
    }

    @Override
    public List<RiskDefinition> findRiskDefinitions(AggregateId contractId) { // todo: pobierać z bazy danych lub z pliku
        return List.of(
                RiskDefinition.builder()
                        .id(1L)
                        .premiumDivisor(new Divisor(10))
                        .build(),
                RiskDefinition.builder()
                        .id(2L)
                        .premiumDivisor(new Divisor(30))
                        .build(),
                RiskDefinition.builder()
                        .id(3L)
                        .premiumDivisor(new Divisor(60))
                        .build()
        );
    }

}