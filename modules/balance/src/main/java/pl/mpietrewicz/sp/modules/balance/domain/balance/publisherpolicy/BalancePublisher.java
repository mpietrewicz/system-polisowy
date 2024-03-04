package pl.mpietrewicz.sp.modules.balance.domain.balance.publisherpolicy;

import pl.mpietrewicz.sp.ddd.annotations.domain.DomainPolicy;
import pl.mpietrewicz.sp.ddd.canonicalmodel.events.BalanceUpdatedEvent;
import pl.mpietrewicz.sp.ddd.canonicalmodel.publishedlanguage.AggregateId;
import pl.mpietrewicz.sp.ddd.canonicalmodel.publishedlanguage.MonthlyBalance;
import pl.mpietrewicz.sp.ddd.canonicalmodel.publishedlanguage.snapshot.premium.PremiumSnapshot;
import pl.mpietrewicz.sp.ddd.support.domain.DomainEventPublisher;
import pl.mpietrewicz.sp.modules.balance.domain.balance.PeriodProvider;
import pl.mpietrewicz.sp.modules.contract.application.api.PremiumService;

import javax.inject.Inject;
import java.time.LocalDateTime;
import java.util.List;

@DomainPolicy
public class BalancePublisher implements PublishPolicy {

    @Inject
    protected DomainEventPublisher eventPublisher;

    @Inject
    protected PremiumService premiumService;

    @Override
    public void doPublish(AggregateId contractId, PeriodProvider before, PeriodProvider after) {
        PremiumSnapshot premiumSnapshot = premiumService.getPremiumSnapshot(contractId, LocalDateTime.now());
        List<MonthlyBalance> monthlyBalances = after.getMonthlyBalances(premiumSnapshot);
        if (monthlyBalances.isEmpty()) return;

        BalanceUpdatedEvent event = createEvent(contractId, monthlyBalances);
        eventPublisher.publish(event, "BalanceServiceImpl");
    }

    private BalanceUpdatedEvent createEvent(AggregateId contractId, List<MonthlyBalance> monthlyBalances) {
        return new BalanceUpdatedEvent(contractId, monthlyBalances);
    }

}