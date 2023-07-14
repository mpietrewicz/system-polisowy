package pl.mpietrewicz.sp.modules.oldbalance.domain.balance;

import pl.mpietrewicz.sp.ddd.annotations.domain.DomainRepository;
import pl.mpietrewicz.sp.ddd.canonicalmodel.publishedlanguage.AggregateId;

@DomainRepository
public interface BalanceRepository {

    Balance load(AggregateId balanceId);

    void save(Balance balance);

    Balance findByContractId(AggregateId contractId);

    Balance findByComponentId(AggregateId componentId);
}