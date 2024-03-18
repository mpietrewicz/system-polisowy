package pl.mpietrewicz.sp.modules.contract.infrastructure.repo.impl;

import lombok.RequiredArgsConstructor;
import pl.mpietrewicz.sp.ddd.annotations.domain.DomainRepositoryImpl;
import pl.mpietrewicz.sp.ddd.canonicalmodel.publishedlanguage.AggregateId;
import pl.mpietrewicz.sp.ddd.support.infrastructure.repo.GenericJpaRepository;
import pl.mpietrewicz.sp.modules.contract.domain.component.Component;
import pl.mpietrewicz.sp.modules.contract.infrastructure.repo.ComponentRepository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;
import java.util.Optional;

@DomainRepositoryImpl
@RequiredArgsConstructor
public class JpaComponentRepository extends GenericJpaRepository<Component> implements ComponentRepository {

    private final SpringDataComponentRepository springDataBalanceRepository;

    @PersistenceContext(unitName = "contract")
    public EntityManager entityManager;

    @Override
    public EntityManager getEntityManager() {
        return entityManager;
    }

    @Override
    public List<Component> findBy(AggregateId contractId) {
        return springDataBalanceRepository.findByContractId(contractId);
    }

    @Override
    public Optional<Component> findBy(AggregateId contractId, String name) {
        return springDataBalanceRepository.findByContractIdAndName(contractId, name);
    }

    @Override
    public Optional<Component> findBy(AggregateId contractId, AggregateId componentId) {
        return springDataBalanceRepository.findByContractIdAndAggregateId(contractId, componentId);
    }

}