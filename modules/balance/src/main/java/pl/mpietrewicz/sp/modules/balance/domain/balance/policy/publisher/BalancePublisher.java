package pl.mpietrewicz.sp.modules.balance.domain.balance.policy.publisher;

import pl.mpietrewicz.sp.ddd.annotations.domain.DomainPolicy;
import pl.mpietrewicz.sp.ddd.canonicalmodel.events.BalanceUpdatedEvent;
import pl.mpietrewicz.sp.ddd.canonicalmodel.publishedlanguage.AggregateId;
import pl.mpietrewicz.sp.ddd.canonicalmodel.publishedlanguage.MonthlyBalance;
import pl.mpietrewicz.sp.ddd.support.domain.DomainEventPublisher;
import pl.mpietrewicz.sp.modules.balance.domain.balance.period.PeriodProvider;
import pl.mpietrewicz.sp.modules.contract.application.api.PremiumService;

import javax.inject.Inject;
import java.util.List;

@DomainPolicy
public class BalancePublisher implements PublishPolicy {

    @Inject
    protected DomainEventPublisher eventPublisher;

    @Inject
    protected PremiumService premiumService;

    @Override
    public void doPublish(AggregateId contractId, PeriodProvider periodProvider) {
        List<MonthlyBalance> monthlyBalances = periodProvider.getMonthlyBalances();
        if (!monthlyBalances.isEmpty()) {
            BalanceUpdatedEvent event = createEvent(contractId, monthlyBalances);
            eventPublisher.publish(event);
        }
    }

    private BalanceUpdatedEvent createEvent(AggregateId contractId, List<MonthlyBalance> monthlyBalances) {
        return new BalanceUpdatedEvent(contractId, monthlyBalances);
    }

}