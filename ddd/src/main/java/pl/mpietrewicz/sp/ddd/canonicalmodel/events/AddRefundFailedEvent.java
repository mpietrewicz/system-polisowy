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
public class AddRefundFailedEvent implements Serializable {

    private final AggregateId refundId;
    private final PositiveAmount refund;
    private final LocalDate date;
    private final Exception exception;

}