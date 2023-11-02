package pl.mpietrewicz.sp.modules.contract.infrastructure.repo.impl;

import pl.mpietrewicz.sp.ddd.annotations.domain.DomainRepositoryImpl;
import pl.mpietrewicz.sp.ddd.canonicalmodel.publishedlanguage.AggregateId;
import pl.mpietrewicz.sp.modules.contract.ddd.support.infrastructure.repository.jpa.GenericJpaRepository;
import pl.mpietrewicz.sp.modules.contract.domain.component.Component;
import pl.mpietrewicz.sp.modules.contract.infrastructure.repo.ComponentRepository;

import java.util.List;

@DomainRepositoryImpl
public class JpaComponentRepository extends GenericJpaRepository<Component> implements ComponentRepository {

    @Override
    public List<Component> findByContractId(AggregateId contractId) {
        String query = "SELECT cp FROM Component cp WHERE cp.contractData.aggregateId = :contractId";
        return entityManager.createQuery(query, Component.class)
                .setParameter("contractId", contractId)
                .getResultList();
    }
}