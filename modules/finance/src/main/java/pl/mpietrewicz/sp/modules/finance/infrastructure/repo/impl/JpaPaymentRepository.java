
package pl.mpietrewicz.sp.modules.finance.infrastructure.repo.impl;

import pl.mpietrewicz.sp.ddd.annotations.domain.DomainRepositoryImpl;
import pl.mpietrewicz.sp.ddd.support.infrastructure.repository.jpa.GenericJpaRepository;
import pl.mpietrewicz.sp.ddd.canonicalmodel.publishedlanguage.AggregateId;
import pl.mpietrewicz.sp.modules.finance.domain.payment.RegisterPayment;
import pl.mpietrewicz.sp.modules.finance.domain.payment.PaymentRepository;

import java.util.List;

@DomainRepositoryImpl
public class JpaPaymentRepository extends GenericJpaRepository<RegisterPayment> implements PaymentRepository {

    @Override
    public List<RegisterPayment> findAll(AggregateId contractId) {
        String query = "SELECT p FROM Payment p WHERE p.contractData.aggregateId = :contractId";
        return entityManager.createQuery(query, RegisterPayment.class)
                .setParameter("contractId", contractId)
                .getResultList();
    }

}