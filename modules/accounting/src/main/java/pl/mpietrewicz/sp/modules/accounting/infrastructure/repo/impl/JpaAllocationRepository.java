package pl.mpietrewicz.sp.modules.accounting.infrastructure.repo.impl;

import lombok.RequiredArgsConstructor;
import pl.mpietrewicz.sp.ddd.annotations.domain.DomainRepositoryImpl;
import pl.mpietrewicz.sp.ddd.canonicalmodel.publishedlanguage.AggregateId;
import pl.mpietrewicz.sp.ddd.canonicalmodel.publishedlanguage.RiskDefinition;
import pl.mpietrewicz.sp.ddd.sharedkernel.valueobject.Divisor;
import pl.mpietrewicz.sp.ddd.support.infrastructure.repo.GenericJpaRepository;
import pl.mpietrewicz.sp.modules.accounting.domain.allocation.Allocation;
import pl.mpietrewicz.sp.modules.accounting.infrastructure.repo.AllocationRepository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;
import java.util.Optional;

@DomainRepositoryImpl
@RequiredArgsConstructor
public class JpaAllocationRepository extends GenericJpaRepository<Allocation> implements AllocationRepository {

    private final SpringDataAllocationRepository springDataAllocationRepository;

    @PersistenceContext(unitName = "accounting")
    public EntityManager entityManager;

    @Override
    public EntityManager getEntityManager() {
        return entityManager;
    }

    @Override
    public Optional<Allocation> findByContractId(AggregateId contractId) {
        return springDataAllocationRepository.findByContractId(contractId);
    }

    @Override
    public List<RiskDefinition> findRiskDefinitions(AggregateId contractId) {
        return List.of(
                RiskDefinition.builder()
                        .id(1L)
                        .name("joint dislocation")
                        .premiumDivisor(new Divisor(10))
                        .build(),
                RiskDefinition.builder()
                        .id(2L)
                        .name("broken bones")
                        .premiumDivisor(new Divisor(20))
                        .build(),
                RiskDefinition.builder()
                        .id(3L)
                        .name("cancer disease")
                        .premiumDivisor(new Divisor(70))
                        .build()
        );
    }

}