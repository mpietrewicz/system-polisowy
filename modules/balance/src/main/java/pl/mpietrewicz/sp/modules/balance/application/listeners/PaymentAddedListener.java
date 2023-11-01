package pl.mpietrewicz.sp.modules.balance.application.listeners;

import lombok.RequiredArgsConstructor;
import pl.mpietrewicz.sp.ddd.annotations.event.EventListener;
import pl.mpietrewicz.sp.ddd.annotations.event.EventListeners;
import pl.mpietrewicz.sp.ddd.canonicalmodel.events.PaymentAddedEvent;
import pl.mpietrewicz.sp.ddd.canonicalmodel.publishedlanguage.snapshot.PaymentData;
import pl.mpietrewicz.sp.ddd.canonicalmodel.publishedlanguage.PaymentPolicy;
import pl.mpietrewicz.sp.modules.balance.application.api.BalanceService;

@EventListeners
@RequiredArgsConstructor
public class PaymentAddedListener {

    private final BalanceService balanceService;

    @EventListener
    public void handle(PaymentAddedEvent event) {
        PaymentData paymentData = event.getPaymentData();
        PaymentPolicy paymentPolicy = event.getPaymentPolicy();

        balanceService.addPayment(paymentData, paymentPolicy);
    }

}