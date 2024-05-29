package pl.mpietrewicz.sp.modules.balance.domain.balance.policy.publisher;

import pl.mpietrewicz.sp.ddd.annotations.domain.DomainPolicy;
import pl.mpietrewicz.sp.ddd.canonicalmodel.events.RenewalAddedEvent;
import pl.mpietrewicz.sp.ddd.canonicalmodel.publishedlanguage.AggregateId;
import pl.mpietrewicz.sp.ddd.support.domain.DomainEventPublisher;
import pl.mpietrewicz.sp.modules.balance.domain.balance.period.PeriodProvider;

import javax.inject.Inject;
import java.time.YearMonth;
import java.util.List;

@DomainPolicy
public class RenewalsPublisher implements PublishPolicy {

    @Inject
    protected DomainEventPublisher eventPublisher;

    @Override
    public void doPublish(AggregateId contractId, PeriodProvider periodProvider) {
        for (YearMonth renewalMonth : periodProvider.getRenewalMonths()) {
            RenewalAddedEvent event = createEvent(contractId, renewalMonth);
            eventPublisher.publish(event);
        }
    }

    private RenewalAddedEvent createEvent(AggregateId contractId, YearMonth renewalMonth) {
        return new RenewalAddedEvent(contractId, renewalMonth);
    }

}