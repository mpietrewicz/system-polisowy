package pl.mpietrewicz.sp.modules.contract.impl;

import pl.mpietrewicz.sp.ddd.annotations.domain.DomainRepositoryImpl;
import pl.mpietrewicz.sp.ddd.support.infrastructure.repository.jpa.GenericJpaRepository;
import pl.mpietrewicz.sp.modules.contract.domain.premium.Premium;
import pl.mpietrewicz.sp.modules.contract.domain.premium.PremiumRepository;
import pl.mpietrewicz.sp.ddd.canonicalmodel.publishedlanguage.AggregateId;

@DomainRepositoryImpl
public class JpaPremiumRepository extends GenericJpaRepository<Premium> implements PremiumRepository {

    @Override
    public Premium findByComponentId(AggregateId componentId) {
        String query = "SELECT p FROM Premium p WHERE p.componentData.aggregateId = :componentId"; // todo: takie zapytania przyspażają dużo problemów
        return entityManager.createQuery(query, Premium.class)
                .setParameter("componentId", componentId)
                .getSingleResult();
    }

}