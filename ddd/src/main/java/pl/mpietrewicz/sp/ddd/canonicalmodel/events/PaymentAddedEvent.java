package pl.mpietrewicz.sp.ddd.canonicalmodel.events;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import pl.mpietrewicz.sp.ddd.annotations.event.Event;
import pl.mpietrewicz.sp.ddd.canonicalmodel.publishedlanguage.snapshot.PaymentData;
import pl.mpietrewicz.sp.ddd.canonicalmodel.publishedlanguage.PaymentPolicy;

import java.io.Serializable;

@SuppressWarnings("serial")
@Event
@Getter
@RequiredArgsConstructor
public class PaymentAddedEvent implements Serializable {

    private final transient PaymentData paymentData;
    private final PaymentPolicy paymentPolicy;

}