package pl.mpietrewicz.sp.modules.accounting.domain.allocation;

import lombok.NoArgsConstructor;
import pl.mpietrewicz.sp.ddd.annotations.domain.ValueObject;
import pl.mpietrewicz.sp.ddd.canonicalmodel.publishedlanguage.RiskDefinition;
import pl.mpietrewicz.sp.modules.accounting.ddd.support.domain.BaseEntity;

import javax.persistence.Entity;
import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;

@ValueObject
@Entity
@NoArgsConstructor
public class Risk extends BaseEntity {

    private Long riskId;
    private BigDecimal amount;

    public Risk(Long riskId, BigDecimal amount) {
        this.riskId = riskId;
        this.amount = amount;
    }

    public Risk getNegate() {
        return new Risk(riskId, amount.negate());
    }

    public boolean isAppliesTo(RiskDefinition riskDefinition) {
        return Objects.equals(riskId, riskDefinition.getId());
    }

    public boolean isAppliesTo(List<RiskDefinition> riskDefinitions) {
        return riskDefinitions.stream()
                .anyMatch(this::isAppliesTo);
    }

    public BigDecimal getAmount() {
        return amount;
    }

}