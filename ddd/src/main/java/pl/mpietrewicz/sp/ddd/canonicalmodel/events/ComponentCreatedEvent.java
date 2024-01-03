package pl.mpietrewicz.sp.ddd.canonicalmodel.events;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import pl.mpietrewicz.sp.ddd.annotations.event.Event;
import pl.mpietrewicz.sp.ddd.canonicalmodel.publishedlanguage.snapshot.ComponentData;
import pl.mpietrewicz.sp.ddd.sharedkernel.Amount;

import java.io.Serializable;

@SuppressWarnings("serial")
@Event
@Getter
@RequiredArgsConstructor
public class ComponentCreatedEvent implements Serializable {

    private final ComponentData componentData;
    private final Amount premium;

}