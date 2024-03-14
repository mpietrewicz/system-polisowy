package pl.mpietrewicz.sp.ddd.canonicalmodel.events;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import pl.mpietrewicz.sp.ddd.annotations.event.Event;
import pl.mpietrewicz.sp.ddd.canonicalmodel.publishedlanguage.snapshot.ContractData;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;

@SuppressWarnings("serial")
@Event
@Getter
@RequiredArgsConstructor
public class PremiumChangedEvent implements Serializable {

    private final ContractData contractData;
    private final LocalDate date;
    private final LocalDateTime timestamp;

}