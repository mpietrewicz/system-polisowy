package pl.mpietrewicz.sp.modules.balance.infrastructure.repo.impl;

import pl.mpietrewicz.sp.ddd.annotations.domain.DomainRepositoryImpl;
import pl.mpietrewicz.sp.ddd.canonicalmodel.publishedlanguage.AggregateId;
import pl.mpietrewicz.sp.modules.balance.ddd.support.infrastructure.repository.jpa.GenericJpaRepository;
import pl.mpietrewicz.sp.modules.balance.domain.balance.Balance;
import pl.mpietrewicz.sp.modules.balance.infrastructure.repo.BalanceRepository;

@DomainRepositoryImpl
public class JpaBalanceRepository extends GenericJpaRepository<Balance> implements BalanceRepository {

    @Override
    public Balance findByContractId(AggregateId contractId) {
        String query = "SELECT b.aggregateId FROM Balance b WHERE b.contractData.aggregateId = :contractId";
        return load(entityManager.createQuery(query, AggregateId.class)
                .setParameter("contractId", contractId)
                .getSingleResult());
    }

}