package pl.mpietrewicz.sp.ddd.canonicalmodel.events;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import pl.mpietrewicz.sp.ddd.annotations.event.Event;
import pl.mpietrewicz.sp.ddd.canonicalmodel.publishedlanguage.AggregateId;
import pl.mpietrewicz.sp.ddd.sharedkernel.valueobject.PositiveAmount;

import java.io.Serializable;
import java.time.LocalDate;

@Event(boundedContext = "balance")
@Getter
@RequiredArgsConstructor
public class AddPaymentFailedEvent implements Serializable {

    private final AggregateId paymentId;
    private final LocalDate date;
    private final PositiveAmount payment;
    private final Exception exception;

}