package pl.mpietrewicz.sp.ddd.canonicalmodel.events;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import pl.mpietrewicz.sp.ddd.annotations.event.Event;
import pl.mpietrewicz.sp.ddd.canonicalmodel.publishedlanguage.snapshot.ContractData;

import java.io.Serializable;
import java.time.LocalDate;

@SuppressWarnings("serial")
@Event
@Getter
@RequiredArgsConstructor
public class ContractEndedEvent implements Serializable {

    private final transient ContractData contractData;
    private final LocalDate date;

}