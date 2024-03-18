package pl.mpietrewicz.sp.modules.balance.application.listeners;

import lombok.RequiredArgsConstructor;
import pl.mpietrewicz.sp.ddd.annotations.event.EventListener;
import pl.mpietrewicz.sp.ddd.annotations.event.EventListeners;
import pl.mpietrewicz.sp.ddd.canonicalmodel.events.ContractEndedEvent;
import pl.mpietrewicz.sp.ddd.canonicalmodel.publishedlanguage.snapshot.ContractData;
import pl.mpietrewicz.sp.modules.balance.application.api.BalanceService;

import java.time.LocalDate;

@EventListeners
@RequiredArgsConstructor
public class ContractEndedListener {

    private final BalanceService balanceService;

    @EventListener
    public void handle(ContractEndedEvent event) {
        ContractData contractData = event.getContractData();
        LocalDate date = event.getDate();

        balanceService.stopCalculating(contractData, date);
    }

}