package pl.mpietrewicz.sp.ddd.canonicalmodel.events;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import pl.mpietrewicz.sp.ddd.annotations.event.Event;
import pl.mpietrewicz.sp.ddd.canonicalmodel.publishedlanguage.snapshot.ContractData;
import pl.mpietrewicz.sp.ddd.canonicalmodel.publishedlanguage.snapshot.premium.PremiumSnapshot;

import java.io.Serializable;

@Event(boundedContext = "contract")
@Getter
@RequiredArgsConstructor
public class ContractCreatedEvent implements Serializable {

    private final transient ContractData contractData;
    private final transient PremiumSnapshot premiumSnapshot;

}