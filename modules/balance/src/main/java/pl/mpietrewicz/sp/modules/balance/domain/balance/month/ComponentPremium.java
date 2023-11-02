package pl.mpietrewicz.sp.modules.balance.domain.balance.month;

import lombok.NoArgsConstructor;
import pl.mpietrewicz.sp.ddd.annotations.domain.ValueObject;
import pl.mpietrewicz.sp.ddd.canonicalmodel.publishedlanguage.AggregateId;
import pl.mpietrewicz.sp.modules.balance.ddd.support.domain.BaseEntity;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import java.math.BigDecimal;

@ValueObject
@Entity
@NoArgsConstructor
public class ComponentPremium extends BaseEntity {

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "aggregateId", column = @Column(name = "contractId", nullable = false))})
    private AggregateId componentId; // todo: te wartości powinny być final
    private BigDecimal amount; // todo: te wartości powinny być final

    public ComponentPremium(AggregateId componentId, BigDecimal amount) {
        if (componentId == null) {
            this.componentId = AggregateId.generate();
        } else {
            this.componentId = componentId;
        }
        this.amount = amount;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public boolean isAppliedTo(ComponentPremium componentPremium) {
        return this.componentId.equals(componentPremium.componentId);
    }

    protected AggregateId getComponentId() {
        return componentId;
    }

}