package pl.mpietrewicz.sp.ddd.canonicalmodel.events;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import pl.mpietrewicz.sp.ddd.annotations.event.Event;
import pl.mpietrewicz.sp.ddd.canonicalmodel.publishedlanguage.AggregateId;
import pl.mpietrewicz.sp.ddd.sharedkernel.Amount;

import java.io.Serializable;
import java.time.LocalDate;

@SuppressWarnings("serial")
@Event
@Getter
@RequiredArgsConstructor
public class AddPaymentFailedEvent implements Serializable {

    private final AggregateId paymentId;
    private final LocalDate date;
    private final Amount amount;
    private final Exception exception;

}