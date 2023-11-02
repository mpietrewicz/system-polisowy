package pl.mpietrewicz.sp.modules.accounting.application.listeners;

import lombok.RequiredArgsConstructor;
import pl.mpietrewicz.sp.ddd.annotations.event.EventListener;
import pl.mpietrewicz.sp.ddd.annotations.event.EventListeners;
import pl.mpietrewicz.sp.ddd.canonicalmodel.events.BalanceUpdatedEvent;
import pl.mpietrewicz.sp.ddd.canonicalmodel.publishedlanguage.MonthlyBalance;
import pl.mpietrewicz.sp.ddd.canonicalmodel.publishedlanguage.snapshot.ContractData;
import pl.mpietrewicz.sp.modules.accounting.application.api.AllocationService;

import java.util.List;

@EventListeners
@RequiredArgsConstructor
public class BalanceUpdatedListener {

    private final AllocationService allocationService;

    @EventListener
    public void handle(BalanceUpdatedEvent event) {
        ContractData contractData = event.getContractData();
        List<MonthlyBalance> monthlyBalances = event.getMonthlyBalances();

        allocationService.update(contractData, monthlyBalances);
    }

}