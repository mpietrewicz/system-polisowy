package pl.mpietrewicz.sp.modules.contract.infrastructure.repo.impl;

import lombok.RequiredArgsConstructor;
import pl.mpietrewicz.sp.ddd.annotations.domain.DomainRepositoryImpl;
import pl.mpietrewicz.sp.ddd.canonicalmodel.publishedlanguage.AggregateId;
import pl.mpietrewicz.sp.ddd.support.infrastructure.repo.GenericJpaRepository;
import pl.mpietrewicz.sp.modules.contract.domain.premium.Premium;
import pl.mpietrewicz.sp.modules.contract.infrastructure.repo.PremiumRepository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@DomainRepositoryImpl
@RequiredArgsConstructor
public class JpaPremiumRepository extends GenericJpaRepository<Premium> implements PremiumRepository {

    private final SpringDataPremiumRepository springDataPremiumRepository;

    @PersistenceContext(unitName = "contract")
    public EntityManager entityManager;

    @Override
    public EntityManager getEntityManager() {
        return entityManager;
    }

    @Override
    public Premium findBy(AggregateId contractId) {
        return springDataPremiumRepository.findByContractId(contractId)
                .orElseThrow();
    }

    @Override
    public Premium findByComponentId(AggregateId componentId) {
        String query = "SELECT p FROM Premium p JOIN p.componentPremiums cp WHERE cp.componentId = :componentId";
        return entityManager.createQuery(query, Premium.class)
                .setParameter("componentId", componentId)
                .getResultStream()
                .findAny()
                .orElseThrow();
    }

}