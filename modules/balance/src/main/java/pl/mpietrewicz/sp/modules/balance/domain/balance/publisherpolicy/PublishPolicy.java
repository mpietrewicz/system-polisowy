package pl.mpietrewicz.sp.modules.balance.domain.balance.publisherpolicy;

import pl.mpietrewicz.sp.ddd.canonicalmodel.publishedlanguage.AggregateId;
import pl.mpietrewicz.sp.modules.balance.domain.balance.PeriodProvider;

public interface PublishPolicy {

    void doPublish(AggregateId contractId, PeriodProvider before, PeriodProvider after);

}