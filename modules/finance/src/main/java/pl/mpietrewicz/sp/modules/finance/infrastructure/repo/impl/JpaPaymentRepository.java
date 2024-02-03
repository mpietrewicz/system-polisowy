
package pl.mpietrewicz.sp.modules.finance.infrastructure.repo.impl;

import pl.mpietrewicz.sp.ddd.annotations.domain.DomainRepositoryImpl;
import pl.mpietrewicz.sp.ddd.canonicalmodel.publishedlanguage.AggregateId;
import pl.mpietrewicz.sp.ddd.support.infrastructure.repo.GenericJpaRepository;
import pl.mpietrewicz.sp.modules.finance.domain.payment.RegisterPayment;
import pl.mpietrewicz.sp.modules.finance.infrastructure.repo.PaymentRepository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;

@DomainRepositoryImpl
public class JpaPaymentRepository extends GenericJpaRepository<RegisterPayment> implements PaymentRepository {

    @PersistenceContext(unitName = "finance")
    public EntityManager entityManager;

    @Override
    public EntityManager getEntityManager() {
        return entityManager;
    }

    @Override
    public List<RegisterPayment> findAll(AggregateId contractId) {
        String query = "SELECT p FROM Payment p WHERE p.contractData.aggregateId = :contractId";
        return entityManager.createQuery(query, RegisterPayment.class)
                .setParameter("contractId", contractId)
                .getResultList();
    }

}