package pl.mpietrewicz.sp.ddd.canonicalmodel.events;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import pl.mpietrewicz.sp.ddd.annotations.event.Event;
import pl.mpietrewicz.sp.ddd.canonicalmodel.publishedlanguage.AggregateId;

import java.io.Serializable;

@SuppressWarnings("serial")
@Event
@Getter
@RequiredArgsConstructor
public class StopBalanceFailedEvent implements Serializable {

    private final AggregateId contractId;
    private final Exception exception;

}