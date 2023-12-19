package pl.mpietrewicz.sp.modules.balance.application.listeners;

import lombok.RequiredArgsConstructor;
import pl.mpietrewicz.sp.ddd.annotations.event.EventListener;
import pl.mpietrewicz.sp.ddd.annotations.event.EventListeners;
import pl.mpietrewicz.sp.ddd.canonicalmodel.events.ComponentCreatedEvent;
import pl.mpietrewicz.sp.ddd.canonicalmodel.publishedlanguage.snapshot.ComponentData;
import pl.mpietrewicz.sp.modules.balance.application.api.BalanceService;

import java.math.BigDecimal;

@EventListeners
@RequiredArgsConstructor
public class ComponentCreatedListener {

    private final BalanceService balanceService;

    @EventListener
    public void handle(ComponentCreatedEvent event) {
        ComponentData componentData = event.getComponentData();
        BigDecimal premium = event.getPremium();

        balanceService.changePremium(componentData, premium);
    }
}