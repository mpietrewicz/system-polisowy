package pl.mpietrewicz.sp.modules.balance.domain.balance.policy.publisher;

import pl.mpietrewicz.sp.ddd.annotations.domain.DomainPolicy;
import pl.mpietrewicz.sp.ddd.canonicalmodel.events.RenewalAddedEvent;
import pl.mpietrewicz.sp.ddd.canonicalmodel.publishedlanguage.AggregateId;
import pl.mpietrewicz.sp.ddd.support.domain.DomainEventPublisher;
import pl.mpietrewicz.sp.modules.balance.domain.balance.PeriodProvider;

import javax.inject.Inject;
import java.time.YearMonth;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@DomainPolicy
public class RenewalsPublisher implements PublishPolicy {

    @Inject
    protected DomainEventPublisher eventPublisher;

    @Override
    public void doPublish(AggregateId contractId, PeriodProvider before, PeriodProvider after) {
        List<YearMonth> allRenewals = getAllRenewals(before, after);
        List<YearMonth> newRenewals = getNewRenewals(allRenewals);

        for (YearMonth renewalMonth : newRenewals) {
            RenewalAddedEvent event = createEvent(contractId, renewalMonth);
            eventPublisher.publish(event);
        }
    }

    private List<YearMonth> getAllRenewals(PeriodProvider before, PeriodProvider after) {
        return Stream.of(before.getRenewalMonths(), after.getRenewalMonths())
                .flatMap(Collection::stream)
                .collect(Collectors.toList());
    }

    private List<YearMonth> getNewRenewals(List<YearMonth> allRenewals) {
        return allRenewals.stream()
                .filter(renual -> Collections.frequency(allRenewals, renual) == 1)
                .collect(Collectors.toList());
    }

    private RenewalAddedEvent createEvent(AggregateId contractId, YearMonth renewalMonth) {
        return new RenewalAddedEvent(contractId, renewalMonth);
    }

}