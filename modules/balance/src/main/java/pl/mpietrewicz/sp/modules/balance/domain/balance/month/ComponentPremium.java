package pl.mpietrewicz.sp.modules.balance.domain.balance.month;

import lombok.NoArgsConstructor;
import pl.mpietrewicz.sp.ddd.annotations.domain.ValueObject;
import pl.mpietrewicz.sp.ddd.canonicalmodel.publishedlanguage.AggregateId;
import pl.mpietrewicz.sp.ddd.sharedkernel.Amount;
import pl.mpietrewicz.sp.modules.balance.ddd.support.domain.BaseEntity;

import javax.persistence.AttributeOverride;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;

@ValueObject
@Entity
@NoArgsConstructor
public class ComponentPremium extends BaseEntity {

    @Embedded
    @AttributeOverride(name = "aggregateId", column = @Column(name = "componentId", nullable = false))
    private AggregateId componentId;

    @Embedded
    @AttributeOverride(name = "value", column = @Column(name = "premium"))
    private Amount premium;

    public ComponentPremium(AggregateId componentId, Amount premium) {
        if (componentId == null) {
            this.componentId = AggregateId.generate();
        } else {
            this.componentId = componentId;
        }
        this.premium = premium;
    }

    public Amount getPremium() {
        return premium;
    }

    public boolean isAppliedTo(ComponentPremium componentPremium) {
        return this.componentId.equals(componentPremium.componentId);
    }

    protected AggregateId getComponentId() {
        return componentId;
    }

    public ComponentPremium createCopy() {
        return new ComponentPremium(componentId, premium);
    }

}