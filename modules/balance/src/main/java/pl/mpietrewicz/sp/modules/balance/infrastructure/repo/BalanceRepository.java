package pl.mpietrewicz.sp.modules.balance.infrastructure.repo;

import pl.mpietrewicz.sp.ddd.annotations.domain.DomainRepository;
import pl.mpietrewicz.sp.ddd.canonicalmodel.publishedlanguage.AggregateId;
import pl.mpietrewicz.sp.modules.balance.domain.balance.Balance;
import pl.mpietrewicz.sp.modules.balance.infrastructure.repo.entity.BalanceEntity;

@DomainRepository
public interface BalanceRepository {

    BalanceEntity load(AggregateId balanceId);

    void save(BalanceEntity balanceEntity);

    void save(Balance balance);

    void merge(Balance balance);

    Balance findByContractIdNew(AggregateId contractId);

}