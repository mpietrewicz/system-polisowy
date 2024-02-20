package pl.mpietrewicz.sp.modules.balance.infrastructure.repo.impl;

import pl.mpietrewicz.sp.ddd.annotations.domain.DomainRepositoryImpl;
import pl.mpietrewicz.sp.ddd.canonicalmodel.publishedlanguage.AggregateId;
import pl.mpietrewicz.sp.ddd.support.infrastructure.repo.GenericJpaRepository;
import pl.mpietrewicz.sp.modules.balance.domain.balance.Balance;
import pl.mpietrewicz.sp.modules.balance.infrastructure.repo.BalanceRepository;
import pl.mpietrewicz.sp.modules.balance.infrastructure.repo.converter.BalanceConverter;
import pl.mpietrewicz.sp.modules.balance.infrastructure.repo.entity.BalanceEntity;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@DomainRepositoryImpl
public class JpaBalanceRepository extends GenericJpaRepository<BalanceEntity> implements BalanceRepository {

    @Inject
    private SpringDataBalanceRepository springDataBalanceRepository;

    @Inject
    private BalanceConverter balanceConverter;

    @PersistenceContext(unitName = "balance")
    public EntityManager entityManager;

    @Override
    public EntityManager getEntityManager() {
        return entityManager;
    }

    @Override
    public void save(Balance balance) {
        BalanceEntity balanceEntity = balanceConverter.convert(balance);
        save(balanceEntity);
    }

    @Override
    public void merge(Balance balance) {
        BalanceEntity balanceEntity = balanceConverter.convert(balance);
        entityManager.merge(balanceEntity);
    }

    @Override
    public Balance findByContractIdNew(AggregateId contractId) {
        BalanceEntity balanceEntity = springDataBalanceRepository.findByContractId(contractId);
        return balanceConverter.convert(balanceEntity);
    }

}