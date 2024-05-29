package pl.mpietrewicz.sp.modules.accounting.domain.allocation;

import lombok.NoArgsConstructor;
import pl.mpietrewicz.sp.ddd.annotations.domain.ValueObject;
import pl.mpietrewicz.sp.ddd.canonicalmodel.publishedlanguage.RiskDefinition;
import pl.mpietrewicz.sp.ddd.sharedkernel.valueobject.PositiveAmount;
import pl.mpietrewicz.sp.ddd.support.infrastructure.repo.BaseEntity;

import javax.persistence.AttributeOverride;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import java.util.List;
import java.util.Objects;

@ValueObject
@Entity
@NoArgsConstructor
public class Risk extends BaseEntity {

    private Long riskId;

    @Embedded
    @AttributeOverride(name = "value", column = @Column(name = "riskAmount"))
    private PositiveAmount riskAmount;

    public Risk(Long riskId, PositiveAmount riskAmount) {
        this.riskId = riskId;
        this.riskAmount = riskAmount;
    }

    public Risk getNegate() {
        return new Risk(riskId, riskAmount.negate());
    }

    public boolean isAppliesTo(RiskDefinition riskDefinition) {
        return Objects.equals(riskId, riskDefinition.getId());
    }

    public boolean isAppliesTo(List<RiskDefinition> riskDefinitions) {
        return riskDefinitions.stream()
                .anyMatch(this::isAppliesTo);
    }

    public PositiveAmount getRiskAmount() {
        return riskAmount;
    }

}