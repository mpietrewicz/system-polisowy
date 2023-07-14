package pl.mpietrewicz.sp.modules.oldbalance.infrastructure.repo.impl;

import pl.mpietrewicz.sp.ddd.annotations.domain.DomainRepositoryImpl;
import pl.mpietrewicz.sp.ddd.support.infrastructure.repository.jpa.GenericJpaRepository;
import pl.mpietrewicz.sp.modules.oldbalance.domain.balance.Balance;
import pl.mpietrewicz.sp.modules.oldbalance.domain.balance.BalanceRepository;
import pl.mpietrewicz.sp.ddd.canonicalmodel.publishedlanguage.AggregateId;

@DomainRepositoryImpl
public class JpaBalanceRepository extends GenericJpaRepository<Balance> implements BalanceRepository {

    @Override
    public Balance findByContractId(AggregateId contractId) {
        String query = "SELECT b FROM Balance b WHERE b.contractData.aggregateId = :contractId";
        return entityManager.createQuery(query, Balance.class)
                .setParameter("contractId", contractId)
                .getSingleResult();
    }

    @Override
    public Balance findByComponentId(AggregateId componentId) {
        String query = "SELECT b FROM Balance b, Component c " +
                "WHERE c.aggregateId = :componentId " +
                "AND b.contractData.aggregateId = c.contractData.aggregateId";
        return entityManager.createQuery(query, Balance.class)
                .setParameter("componentId", componentId)
                .getSingleResult();
    }

}