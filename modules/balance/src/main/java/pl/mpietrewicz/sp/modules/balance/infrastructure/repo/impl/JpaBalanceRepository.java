package pl.mpietrewicz.sp.modules.balance.infrastructure.repo.impl;

import pl.mpietrewicz.sp.ddd.annotations.domain.DomainRepositoryImpl;
import pl.mpietrewicz.sp.ddd.canonicalmodel.publishedlanguage.AggregateId;
import pl.mpietrewicz.sp.ddd.support.infrastructure.repository.jpa.GenericJpaRepository;
import pl.mpietrewicz.sp.modules.balance.domain.balance.Balance;
import pl.mpietrewicz.sp.modules.balance.domain.balance.BalanceRepository;

import java.util.List;

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
    public List<Balance> findAll() {
        String query = "SELECT b FROM Balance";
        return entityManager.createQuery(query, Balance.class)
                .getResultList();
    }

}