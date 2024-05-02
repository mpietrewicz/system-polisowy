package pl.mpietrewicz.sp.modules.balance.domain.balance.policy.publisher;

import pl.mpietrewicz.sp.ddd.canonicalmodel.publishedlanguage.AggregateId;
import pl.mpietrewicz.sp.modules.balance.domain.balance.period.PeriodProvider;

public interface PublishPolicy {

    void doPublish(AggregateId contractId, PeriodProvider before, PeriodProvider after);

}