package pl.mpietrewicz.sp.modules.contract.infrastructure.repo.impl;

import pl.mpietrewicz.sp.ddd.annotations.domain.DomainRepositoryImpl;
import pl.mpietrewicz.sp.ddd.canonicalmodel.publishedlanguage.AggregateId;
import pl.mpietrewicz.sp.ddd.support.infrastructure.repo.GenericJpaRepository;
import pl.mpietrewicz.sp.modules.contract.domain.premium.Premium;
import pl.mpietrewicz.sp.modules.contract.infrastructure.repo.PremiumRepository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@DomainRepositoryImpl
public class JpaPremiumRepository extends GenericJpaRepository<Premium> implements PremiumRepository {

    @PersistenceContext(unitName = "contract")
    public EntityManager entityManager;

    @Override
    public EntityManager getEntityManager() {
        return entityManager;
    }

    @Override
    public Premium findByContractId(AggregateId contractId) {
        String query = "SELECT p FROM Premium p WHERE p.contractData.aggregateId = :contractId";
        return entityManager.createQuery(query, Premium.class)
                .setParameter("contractId", contractId)
                .getSingleResult();
    }

    @Override
    public Premium findByComponentId(AggregateId componentId) {
        String query = "SELECT p FROM Premium p JOIN p.componentPremiums cp WHERE cp.componentData.aggregateId = :componentId";
        return entityManager.createQuery(query, Premium.class)
                .setParameter("componentId", componentId)
                .getResultStream()
                .findAny()
                .orElseThrow();
    }

}