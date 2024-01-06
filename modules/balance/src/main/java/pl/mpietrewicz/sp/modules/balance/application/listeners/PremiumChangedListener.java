package pl.mpietrewicz.sp.modules.balance.application.listeners;

import lombok.RequiredArgsConstructor;
import pl.mpietrewicz.sp.ddd.annotations.event.EventListener;
import pl.mpietrewicz.sp.ddd.annotations.event.EventListeners;
import pl.mpietrewicz.sp.ddd.canonicalmodel.events.PremiumChangedEvent;
import pl.mpietrewicz.sp.ddd.canonicalmodel.publishedlanguage.snapshot.premium.PremiumSnapshot;
import pl.mpietrewicz.sp.modules.balance.application.api.BalanceService;

import java.time.LocalDate;

@EventListeners
@RequiredArgsConstructor
public class PremiumChangedListener {

    private final BalanceService balanceService;

    @EventListener
    public void handle(PremiumChangedEvent event) {
        LocalDate date = event.getDate();
        PremiumSnapshot premiumSnapshot = event.getPremiumSnapshot();

        balanceService.changePremium(date, premiumSnapshot);
    }
}