package pl.mpietrewicz.sp.modules.balance.application.listeners;

import lombok.RequiredArgsConstructor;
import pl.mpietrewicz.sp.ddd.annotations.event.EventListener;
import pl.mpietrewicz.sp.ddd.annotations.event.EventListeners;
import pl.mpietrewicz.sp.ddd.canonicalmodel.events.RefundAddedEvent;
import pl.mpietrewicz.sp.ddd.canonicalmodel.publishedlanguage.snapshot.RefundData;
import pl.mpietrewicz.sp.modules.balance.application.api.BalanceService;

@EventListeners
@RequiredArgsConstructor
public class RefundAddedListener {

    private final BalanceService balanceService;

    @EventListener
    public void handle(RefundAddedEvent event) {
        RefundData paymentData = event.getPaymentData();

        balanceService.addRefund(paymentData);
    }

}