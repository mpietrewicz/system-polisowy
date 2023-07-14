package pl.mpietrewicz.sp.modules.contract.domain.termination;

import pl.mpietrewicz.sp.ddd.annotations.domain.DomainFactory;
import pl.mpietrewicz.sp.ddd.canonicalmodel.publishedlanguage.AggregateId;
import pl.mpietrewicz.sp.ddd.canonicalmodel.publishedlanguage.snapshot.ComponentData;

import java.time.LocalDate;
import java.time.LocalDateTime;

@DomainFactory
public class TerminationFactory {

    public Termination create(ComponentData componentData, LocalDate terminatedDate) {
        AggregateId aggregateId = AggregateId.generate();
        LocalDateTime registerDate = LocalDateTime.now();
        return new Termination(aggregateId, componentData, terminatedDate, registerDate);
    }

}