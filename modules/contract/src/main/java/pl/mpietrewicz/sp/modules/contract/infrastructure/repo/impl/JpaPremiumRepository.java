package pl.mpietrewicz.sp.modules.contract.infrastructure.repo.impl;

import pl.mpietrewicz.sp.ddd.annotations.domain.DomainRepositoryImpl;
import pl.mpietrewicz.sp.ddd.canonicalmodel.publishedlanguage.AggregateId;
import pl.mpietrewicz.sp.modules.contract.ddd.support.infrastructure.repository.jpa.GenericJpaRepository;
import pl.mpietrewicz.sp.modules.contract.domain.premium.Premium;
import pl.mpietrewicz.sp.modules.contract.infrastructure.repo.PremiumRepository;

@DomainRepositoryImpl
public class JpaPremiumRepository extends GenericJpaRepository<Premium> implements PremiumRepository {

    @Override
    public Premium findByContractId(AggregateId contractId) {
        String query = "SELECT p.aggregateId FROM Premium p WHERE p.contractData.aggregateId = :contractId";
        return load(entityManager.createQuery(query, AggregateId.class)
                .setParameter("contractId", contractId)
                .getSingleResult());
    }

    @Override
    public Premium findByComponentId(AggregateId componentId) {
        String query = "SELECT p.aggregateId FROM Premium p JOIN p.componentPremiums cp WHERE cp.componentData.aggregateId = :componentId";
        return load(entityManager.createQuery(query, AggregateId.class)
                .setParameter("componentId", componentId)
                .getResultStream()
                .findAny()
                .orElseThrow());
    }

}