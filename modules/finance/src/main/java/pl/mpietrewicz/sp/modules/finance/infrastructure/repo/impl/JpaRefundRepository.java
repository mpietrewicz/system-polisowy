
package pl.mpietrewicz.sp.modules.finance.infrastructure.repo.impl;

import lombok.RequiredArgsConstructor;
import pl.mpietrewicz.sp.ddd.annotations.domain.DomainRepositoryImpl;
import pl.mpietrewicz.sp.ddd.canonicalmodel.publishedlanguage.AggregateId;
import pl.mpietrewicz.sp.ddd.support.infrastructure.repo.GenericJpaRepository;
import pl.mpietrewicz.sp.modules.finance.domain.refund.Refund;
import pl.mpietrewicz.sp.modules.finance.infrastructure.repo.RefundRepository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;

@DomainRepositoryImpl
@RequiredArgsConstructor
public class JpaRefundRepository extends GenericJpaRepository<Refund> implements RefundRepository {

    private final SpringDataRefundRepository springDataRefundRepository;

    @PersistenceContext(unitName = "finance")
    public EntityManager entityManager;

    @Override
    public EntityManager getEntityManager() {
        return entityManager;
    }

    @Override
    public List<Refund> findBy(AggregateId contractId) {
        return springDataRefundRepository.findByContractId(contractId);
    }

}