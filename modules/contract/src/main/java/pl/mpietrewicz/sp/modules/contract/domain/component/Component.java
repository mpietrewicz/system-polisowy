package pl.mpietrewicz.sp.modules.contract.domain.component;

import lombok.Getter;
import pl.mpietrewicz.sp.ddd.annotations.domain.AggregateRoot;
import pl.mpietrewicz.sp.ddd.canonicalmodel.publishedlanguage.AggregateId;
import pl.mpietrewicz.sp.ddd.canonicalmodel.publishedlanguage.snapshot.ComponentData;
import pl.mpietrewicz.sp.ddd.support.infrastructure.repo.BaseAggregateRoot;

import javax.persistence.AttributeOverride;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Getter
@AggregateRoot
public class Component extends BaseAggregateRoot {

    @Embedded
    @AttributeOverride(name = "aggregateId", column = @Column(name = "contractId", nullable = false))
    private AggregateId contractId;

    private String name;

    private final LocalDateTime registration = LocalDateTime.now();

    private LocalDate start;

    @Enumerated(EnumType.STRING)
    private ComponentType componentType;

    private LocalDate end;

    public Component() {
    }

    public Component(AggregateId aggregateId, AggregateId contractId, String name, LocalDate start,
                     ComponentType componentType) {
        this.aggregateId = aggregateId;
        this.contractId = contractId;
        this.name = name;
        this.start = start;
        this.componentType = componentType;
    }

    public void terminate(LocalDate terminatedDate) {
        end = terminatedDate;
    }

    public ComponentData generateSnapshot() {
        return new ComponentData(aggregateId, contractId, start);
    }

    public AggregateId getContractId() {
        return contractId;
    }

    public boolean isAdditional() {
        return this.componentType == ComponentType.ADDITIONAL;
    }

}