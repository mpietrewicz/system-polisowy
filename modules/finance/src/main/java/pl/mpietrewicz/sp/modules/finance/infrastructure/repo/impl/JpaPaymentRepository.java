
package pl.mpietrewicz.sp.modules.finance.infrastructure.repo.impl;

import lombok.RequiredArgsConstructor;
import pl.mpietrewicz.sp.ddd.annotations.domain.DomainRepositoryImpl;
import pl.mpietrewicz.sp.ddd.canonicalmodel.publishedlanguage.AggregateId;
import pl.mpietrewicz.sp.ddd.support.infrastructure.repo.GenericJpaRepository;
import pl.mpietrewicz.sp.modules.finance.domain.payment.Payment;
import pl.mpietrewicz.sp.modules.finance.infrastructure.repo.PaymentRepository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;

@DomainRepositoryImpl
@RequiredArgsConstructor
public class JpaPaymentRepository extends GenericJpaRepository<Payment> implements PaymentRepository {

    private final SpringDataPaymentRepository springDataPaymentRepository;

    @PersistenceContext(unitName = "finance")
    public EntityManager entityManager;

    @Override
    public EntityManager getEntityManager() {
        return entityManager;
    }

    @Override
    public List<Payment> findBy(AggregateId contractId) {
        return springDataPaymentRepository.findByContractId(contractId);
    }

    @Override
    public List<Payment> findBy(AggregateId contractId, AggregateId paymentId) {
        return springDataPaymentRepository.findByContractIdAndAggregateId(contractId, paymentId);
    }

}