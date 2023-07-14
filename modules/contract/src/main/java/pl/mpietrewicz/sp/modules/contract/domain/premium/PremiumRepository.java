package pl.mpietrewicz.sp.modules.contract.domain.premium;

import pl.mpietrewicz.sp.ddd.annotations.domain.DomainRepository;
import pl.mpietrewicz.sp.ddd.canonicalmodel.publishedlanguage.AggregateId;

@DomainRepository
public interface PremiumRepository {

    Premium load(AggregateId balanceId);

    void save(Premium balance);

    Premium findByComponentId(AggregateId componentId);
}