package pl.mpietrewicz.sp.ddd.canonicalmodel.events;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import pl.mpietrewicz.sp.ddd.annotations.event.Event;
import pl.mpietrewicz.sp.ddd.canonicalmodel.publishedlanguage.snapshot.premium.PremiumSnapshot;

import java.io.Serializable;
import java.time.LocalDateTime;

@SuppressWarnings("serial")
@Event
@Getter
@RequiredArgsConstructor
public class ChangePremiumFailedEvent implements Serializable {

    private final PremiumSnapshot premiumSnapshot;
    private final Exception exception;

}