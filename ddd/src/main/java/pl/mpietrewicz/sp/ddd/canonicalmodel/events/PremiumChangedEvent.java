package pl.mpietrewicz.sp.ddd.canonicalmodel.events;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import pl.mpietrewicz.sp.ddd.annotations.event.Event;
import pl.mpietrewicz.sp.ddd.canonicalmodel.publishedlanguage.AggregateId;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Event(boundedContext = "contract")
@Getter
@RequiredArgsConstructor
public class PremiumChangedEvent implements Serializable {

    private final AggregateId contractId;
    private final LocalDate date;
    private final LocalDateTime timestamp;

}