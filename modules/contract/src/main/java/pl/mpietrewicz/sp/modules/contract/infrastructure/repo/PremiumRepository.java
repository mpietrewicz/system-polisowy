package pl.mpietrewicz.sp.modules.contract.infrastructure.repo;

import pl.mpietrewicz.sp.ddd.annotations.domain.DomainRepository;
import pl.mpietrewicz.sp.ddd.canonicalmodel.publishedlanguage.AggregateId;
import pl.mpietrewicz.sp.modules.contract.domain.premium.Premium;

@DomainRepository
public interface PremiumRepository {

    Premium load(AggregateId premiumId);

    void save(Premium premium);

    Premium findBy(AggregateId contractId);

    Premium findByComponentId(AggregateId componentId);

}