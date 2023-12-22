package pl.mpietrewicz.sp.modules.balance.application.listeners;

import lombok.RequiredArgsConstructor;
import pl.mpietrewicz.sp.ddd.annotations.event.EventListener;
import pl.mpietrewicz.sp.ddd.annotations.event.EventListeners;
import pl.mpietrewicz.sp.ddd.canonicalmodel.events.ContractCreatedEvent;
import pl.mpietrewicz.sp.ddd.canonicalmodel.publishedlanguage.snapshot.ComponentData;
import pl.mpietrewicz.sp.ddd.canonicalmodel.publishedlanguage.snapshot.ContractData;
import pl.mpietrewicz.sp.ddd.sharedkernel.Amount;
import pl.mpietrewicz.sp.modules.balance.application.api.BalanceService;

@EventListeners
@RequiredArgsConstructor
public class ContractCreatedListener {

    private final BalanceService balanceService;

    @EventListener
    public void handle(ContractCreatedEvent event) {
        ContractData contractData = event.getContractData();
        ComponentData componentData = event.getComponentData();
        Amount premium = event.getPremium();

        balanceService.createBalance(contractData, componentData, premium);
    }

}