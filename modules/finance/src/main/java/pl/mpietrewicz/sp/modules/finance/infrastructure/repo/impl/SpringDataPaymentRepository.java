package pl.mpietrewicz.sp.modules.finance.infrastructure.repo.impl;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.mpietrewicz.sp.ddd.annotations.domain.DomainRepositoryImpl;
import pl.mpietrewicz.sp.ddd.canonicalmodel.publishedlanguage.AggregateId;
import pl.mpietrewicz.sp.modules.finance.domain.payment.Payment;

import java.util.List;

@DomainRepositoryImpl
public interface SpringDataPaymentRepository extends JpaRepository<Payment, String> {

    List<Payment> findByContractId(AggregateId contractId);

    List<Payment> findByContractIdAndAggregateId(AggregateId contractId, AggregateId aggregateId);

}