package pl.mpietrewicz.sp.modules.balance.infrastructure.repo.impl;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.mpietrewicz.sp.ddd.annotations.domain.DomainRepositoryImpl;
import pl.mpietrewicz.sp.ddd.canonicalmodel.publishedlanguage.AggregateId;
import pl.mpietrewicz.sp.modules.balance.infrastructure.repo.entity.BalanceEntity;

@DomainRepositoryImpl
interface SpringDataBalanceRepository extends JpaRepository<BalanceEntity, String> {

    BalanceEntity findByContractId(AggregateId contractId);

}