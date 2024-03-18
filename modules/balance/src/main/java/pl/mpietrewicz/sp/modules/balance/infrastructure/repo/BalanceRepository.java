package pl.mpietrewicz.sp.modules.balance.infrastructure.repo;

import pl.mpietrewicz.sp.ddd.annotations.domain.DomainRepository;
import pl.mpietrewicz.sp.ddd.canonicalmodel.publishedlanguage.AggregateId;
import pl.mpietrewicz.sp.modules.balance.domain.balance.Balance;

@DomainRepository
public interface BalanceRepository {

    Balance load(AggregateId balanceId);

    void save(Balance balance);

    Balance findBy(AggregateId contractId);

}