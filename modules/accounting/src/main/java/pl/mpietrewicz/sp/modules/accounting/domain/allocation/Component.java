package pl.mpietrewicz.sp.modules.accounting.domain.allocation;

import lombok.NoArgsConstructor;
import pl.mpietrewicz.sp.ddd.annotations.domain.ValueObject;
import pl.mpietrewicz.sp.ddd.canonicalmodel.publishedlanguage.AggregateId;
import pl.mpietrewicz.sp.ddd.canonicalmodel.publishedlanguage.RiskDefinition;
import pl.mpietrewicz.sp.modules.accounting.ddd.support.domain.BaseEntity;

import javax.persistence.Entity;
import javax.persistence.OneToMany;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static java.math.BigDecimal.ZERO;
import static java.util.function.Predicate.not;
import static javax.persistence.CascadeType.ALL;

@ValueObject
@Entity(name = "component_allloc")
@NoArgsConstructor
public class Component extends BaseEntity {

    private AggregateId componentId;

    @OneToMany(cascade = ALL)
    private List<Risk> risks;

    public Component(AggregateId componentId, List<Risk> risks) {
        this.componentId = componentId;
        this.risks = risks;
    }

    public boolean isAppliesTo(AggregateId componentId) {
        return this.componentId.equals(componentId);
    }

    public void correct(Map.Entry<AggregateId, BigDecimal> newComponentsPremium, List<RiskDefinition> riskDefinitions) {
        correctCommonsRisks(newComponentsPremium, riskDefinitions);
        correctOtherRisks(riskDefinitions);

//        if (newComponentsPremium.getValue().compareTo(getAmount()) != 0) {
//            throw new IllegalStateException("Nie udało się poprawnie skorygować przypisu!");
//        }
    }

    private void correctCommonsRisks(Map.Entry<AggregateId, BigDecimal> newComponentsPremium, List<RiskDefinition> riskDefinitions) {
        for (RiskDefinition riskDefinition : riskDefinitions) {
            BigDecimal premium = determinePremium(newComponentsPremium, riskDefinition);
            Risk risk = AllocationFactory.createRisk(premium, riskDefinition);
            this.risks.add(risk);
        }
    }

    private void correctOtherRisks(List<RiskDefinition> riskDefinitions) {
        risks.stream()
                .filter(not(risk -> risk.isAppliesTo(riskDefinitions)))
                .forEach(risk -> {
                    Risk negate = risk.getNegate();
                    this.risks.add(negate);
                });
    }

    private BigDecimal determinePremium(Map.Entry<AggregateId, BigDecimal> newComponentsPremium, RiskDefinition riskDefinition) {
        Optional<Risk> currentRisk = getCurrentRisk(riskDefinition);
        if (currentRisk.isEmpty()) {
            return newComponentsPremium.getValue();
        } else {
            return newComponentsPremium.getValue(); // todo było subtract(getAmount()), ale powodowało błędy
        }
    }

    private Optional<Risk> getCurrentRisk(RiskDefinition riskDefinition) {
        return risks.stream()
                .filter(risk -> risk.isAppliesTo(riskDefinition))
                .findAny();
    }

    public BigDecimal getAmount() {
        return risks.stream()
                .map(Risk::getAmount)
                .reduce(ZERO, BigDecimal::add);
    }

}