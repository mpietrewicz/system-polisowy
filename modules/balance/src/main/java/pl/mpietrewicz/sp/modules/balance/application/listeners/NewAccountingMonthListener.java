package pl.mpietrewicz.sp.modules.balance.application.listeners;

import lombok.RequiredArgsConstructor;
import pl.mpietrewicz.sp.ddd.annotations.event.EventListener;
import pl.mpietrewicz.sp.ddd.annotations.event.EventListeners;
import pl.mpietrewicz.sp.ddd.canonicalmodel.events.NewAccountingMonthEvent;
import pl.mpietrewicz.sp.modules.balance.domain.balance.Balance;
import pl.mpietrewicz.sp.modules.balance.domain.balance.BalanceRepository;

import java.time.YearMonth;

@EventListeners
@RequiredArgsConstructor
public class NewAccountingMonthListener {

    private final BalanceRepository balanceRepository;

    @EventListener
    public void handle(NewAccountingMonthEvent event) {
        YearMonth newMonth = event.getMonth();
        for (Balance balance : balanceRepository.findAll()) {
            balance.openMonth(newMonth);
        }
    }

}