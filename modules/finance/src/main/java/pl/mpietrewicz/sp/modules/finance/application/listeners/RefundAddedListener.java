package pl.mpietrewicz.sp.modules.finance.application.listeners;

import lombok.RequiredArgsConstructor;
import pl.mpietrewicz.sp.ddd.annotations.event.EventListener;
import pl.mpietrewicz.sp.ddd.annotations.event.EventListeners;
import pl.mpietrewicz.sp.ddd.canonicalmodel.events.RefundAddedEvent;
import pl.mpietrewicz.sp.ddd.canonicalmodel.publishedlanguage.snapshot.RefundData;
import pl.mpietrewicz.sp.modules.finance.application.api.FinanceService;

@EventListeners
@RequiredArgsConstructor
public class RefundAddedListener {

    private final FinanceService financeService;

    @EventListener
    public void handle(RefundAddedEvent event) {
        RefundData refundData = event.getRefundData();
        financeService.addRefund(refundData);
    }

}