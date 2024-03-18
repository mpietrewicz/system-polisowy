package pl.mpietrewicz.sp.ddd.canonicalmodel.events;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import pl.mpietrewicz.sp.ddd.annotations.event.Event;
import pl.mpietrewicz.sp.ddd.canonicalmodel.publishedlanguage.AggregateId;

import java.io.Serializable;

@Event(boundedContext = "balance")
@Getter
@RequiredArgsConstructor
public class CancelStopBalanceFailedEvent implements Serializable {

    private final AggregateId contractId;
    private final Exception exception;

}