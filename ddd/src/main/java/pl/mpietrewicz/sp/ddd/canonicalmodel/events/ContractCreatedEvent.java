package pl.mpietrewicz.sp.ddd.canonicalmodel.events;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import pl.mpietrewicz.sp.ddd.annotations.event.Event;
import pl.mpietrewicz.sp.ddd.canonicalmodel.publishedlanguage.Frequency;
import pl.mpietrewicz.sp.ddd.canonicalmodel.publishedlanguage.snapshot.ContractData;

import java.io.Serializable;
import java.math.BigDecimal;

@SuppressWarnings("serial")
@Event
@Getter
@RequiredArgsConstructor
public class ContractCreatedEvent implements Serializable {

    private final transient ContractData contractData;
    private final BigDecimal premium;
    private final Frequency frequency;

}