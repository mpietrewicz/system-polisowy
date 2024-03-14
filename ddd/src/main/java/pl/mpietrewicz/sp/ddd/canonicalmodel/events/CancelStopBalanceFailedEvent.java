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
public class CancelStopBalanceFailedEvent implements Serializable {

    private final Exception exception;

}