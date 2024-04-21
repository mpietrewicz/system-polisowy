package pl.mpietrewicz.sp.modules.balance.infrastructure.repo.impl;

import pl.mpietrewicz.sp.ddd.annotations.domain.DomainRepositoryImpl;
import pl.mpietrewicz.sp.ddd.canonicalmodel.publishedlanguage.AggregateId;
import pl.mpietrewicz.sp.ddd.support.infrastructure.repo.GenericJpaRepository;
import pl.mpietrewicz.sp.modules.balance.domain.balance.Balance;
import pl.mpietrewicz.sp.modules.balance.infrastructure.repo.BalanceRepository;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@DomainRepositoryImpl
public class JpaBalanceRepository extends GenericJpaRepository<Balance> implements BalanceRepository {

    @Inject
    SpringDataBalanceRepository springDataBalanceRepository;

    @PersistenceContext(unitName = "balance")
    public EntityManager entityManager;

    @Override
    public EntityManager getEntityManager() {
        return entityManager;
    }

    @Override
    public Balance findByContractId(AggregateId contractId) {
        return springDataBalanceRepository.findByContractId(contractId);
    }

}