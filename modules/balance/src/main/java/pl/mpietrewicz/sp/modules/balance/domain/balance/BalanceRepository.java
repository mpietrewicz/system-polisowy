package pl.mpietrewicz.sp.modules.balance.domain.balance;

import pl.mpietrewicz.sp.ddd.annotations.domain.DomainRepository;
import pl.mpietrewicz.sp.ddd.canonicalmodel.publishedlanguage.AggregateId;

import java.util.List;

@DomainRepository
public interface BalanceRepository {

    Balance load(AggregateId balanceId);

    void save(Balance balance);

    Balance findByContractId(AggregateId contractId);

    List<Balance> findAll();

}