package pl.mpietrewicz.sp.ddd.canonicalmodel.events;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import pl.mpietrewicz.sp.ddd.annotations.event.Event;
import pl.mpietrewicz.sp.ddd.canonicalmodel.publishedlanguage.snapshot.ComponentData;

import java.io.Serializable;
import java.math.BigDecimal;

@SuppressWarnings("serial")
@Event
@Getter
@RequiredArgsConstructor
public class ComponentCreatedEvent implements Serializable {

    private final ComponentData componentData;
    private final BigDecimal premium;

}