package pl.mpietrewicz.sp.ddd.canonicalmodel.events;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import pl.mpietrewicz.sp.ddd.annotations.event.Event;
import pl.mpietrewicz.sp.ddd.canonicalmodel.publishedlanguage.AggregateId;

import java.io.Serializable;
import java.time.YearMonth;

@Event(boundedContext = "balance")
@Getter
@RequiredArgsConstructor
public class RenewalAddedEvent implements Serializable {

    private final transient AggregateId contractId;
    private final transient YearMonth yearMonth;

}