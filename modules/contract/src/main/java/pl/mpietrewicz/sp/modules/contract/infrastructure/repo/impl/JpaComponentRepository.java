package pl.mpietrewicz.sp.modules.contract.infrastructure.repo.impl;

import pl.mpietrewicz.sp.ddd.annotations.domain.DomainRepositoryImpl;
import pl.mpietrewicz.sp.ddd.canonicalmodel.publishedlanguage.AggregateId;
import pl.mpietrewicz.sp.ddd.support.infrastructure.repo.GenericJpaRepository;
import pl.mpietrewicz.sp.modules.contract.domain.component.Component;
import pl.mpietrewicz.sp.modules.contract.infrastructure.repo.ComponentRepository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;

@DomainRepositoryImpl
public class JpaComponentRepository extends GenericJpaRepository<Component> implements ComponentRepository {

    @PersistenceContext(unitName = "contract")
    public EntityManager entityManager;

    @Override
    public EntityManager getEntityManager() {
        return entityManager;
    }

    @Override
    public List<Component> findByContractId(AggregateId contractId) {
        String query = "SELECT cp FROM Component cp WHERE cp.contractData.aggregateId = :contractId";
        return entityManager.createQuery(query, Component.class)
                .setParameter("contractId", contractId)
                .getResultList();
    }

    @Override
    public Component findByNumber(String number) {
        String query = "SELECT cp FROM Component cp WHERE cp.number = :number";
        return entityManager.createQuery(query, Component.class)
                .setParameter("number", number)
                .getSingleResult();
    }

}