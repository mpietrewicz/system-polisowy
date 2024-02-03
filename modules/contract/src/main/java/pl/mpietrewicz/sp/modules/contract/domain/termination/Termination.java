package pl.mpietrewicz.sp.modules.contract.domain.termination;

import pl.mpietrewicz.sp.ddd.annotations.domain.AggregateRoot;
import pl.mpietrewicz.sp.ddd.canonicalmodel.publishedlanguage.AggregateId;
import pl.mpietrewicz.sp.ddd.canonicalmodel.publishedlanguage.snapshot.ComponentData;
import pl.mpietrewicz.sp.ddd.support.infrastructure.repo.BaseAggregateRoot;

import javax.persistence.Embedded;
import javax.persistence.Entity;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@AggregateRoot
public class Termination extends BaseAggregateRoot {

    LocalDate terminatedDate;
    LocalDateTime registerDate;
    @Embedded
    private ComponentData componentData;

    public Termination() {
    }

    public Termination(AggregateId aggregateId, ComponentData componentData, LocalDate terminatedDate,
                       LocalDateTime registerDate) {
        this.aggregateId = aggregateId;
        this.componentData = componentData;
        this.terminatedDate = terminatedDate;
        this.registerDate = registerDate;
    }


}