package pl.mpietrewicz.sp.modules.balance.application.listeners;

import lombok.RequiredArgsConstructor;
import pl.mpietrewicz.sp.ddd.annotations.event.EventListener;
import pl.mpietrewicz.sp.ddd.annotations.event.EventListeners;
import pl.mpietrewicz.sp.ddd.canonicalmodel.events.PremiumChangedEvent;
import pl.mpietrewicz.sp.ddd.canonicalmodel.publishedlanguage.AggregateId;
import pl.mpietrewicz.sp.ddd.canonicalmodel.publishedlanguage.snapshot.ContractData;
import pl.mpietrewicz.sp.modules.balance.application.api.BalanceService;

import java.time.LocalDate;
import java.time.LocalDateTime;

@EventListeners
@RequiredArgsConstructor
public class PremiumChangedListener {

    private final BalanceService balanceService;

    @EventListener
    public void handle(PremiumChangedEvent event) {
        AggregateId contractId = event.getContractId();
        LocalDate date = event.getDate();
        LocalDateTime timestamp = event.getTimestamp();

        balanceService.changePremium(contractId, date, timestamp);
    }
}