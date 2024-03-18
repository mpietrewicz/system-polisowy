package pl.mpietrewicz.sp.ddd.canonicalmodel.events;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import pl.mpietrewicz.sp.ddd.annotations.event.Event;
import pl.mpietrewicz.sp.ddd.canonicalmodel.publishedlanguage.snapshot.RefundData;

import java.io.Serializable;

@Event(boundedContext = "balance")
@Getter
@RequiredArgsConstructor
public class RefundAddedEvent implements Serializable {

    private final transient RefundData refundData;

}