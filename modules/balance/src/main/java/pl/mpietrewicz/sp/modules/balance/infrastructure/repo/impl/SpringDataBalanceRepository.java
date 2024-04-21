package pl.mpietrewicz.sp.modules.balance.infrastructure.repo.impl;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.mpietrewicz.sp.ddd.annotations.domain.DomainRepositoryImpl;
import pl.mpietrewicz.sp.ddd.canonicalmodel.publishedlanguage.AggregateId;
import pl.mpietrewicz.sp.ddd.canonicalmodel.publishedlanguage.snapshot.ContractData;
import pl.mpietrewicz.sp.modules.balance.domain.balance.Balance;

@DomainRepositoryImpl
interface SpringDataBalanceRepository extends JpaRepository<Balance, String> {

    Balance findByContractId(AggregateId contractId);

}