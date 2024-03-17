package pl.mpietrewicz.sp.ddd.canonicalmodel.events;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import pl.mpietrewicz.sp.ddd.annotations.event.Event;
import pl.mpietrewicz.sp.ddd.canonicalmodel.publishedlanguage.AggregateId;
import pl.mpietrewicz.sp.ddd.sharedkernel.Amount;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;

@SuppressWarnings("serial")
@Event
@Getter
@RequiredArgsConstructor
public class ChangePremiumFailedEvent implements Serializable {

    private final AggregateId contractId;
    private final LocalDateTime timestamp;
    private final Exception exception;

}