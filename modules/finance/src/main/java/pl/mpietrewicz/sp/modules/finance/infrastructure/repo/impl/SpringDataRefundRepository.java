package pl.mpietrewicz.sp.modules.finance.infrastructure.repo.impl;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.mpietrewicz.sp.ddd.annotations.domain.DomainRepositoryImpl;
import pl.mpietrewicz.sp.ddd.canonicalmodel.publishedlanguage.AggregateId;
import pl.mpietrewicz.sp.modules.finance.domain.refund.Refund;

import java.util.List;

@DomainRepositoryImpl
public interface SpringDataRefundRepository extends JpaRepository<Refund, String> {

    List<Refund> findByContractId(AggregateId contractId);

}