package pl.mpietrewicz.sp.modules.accounting.domain.allocation;

import lombok.NoArgsConstructor;
import pl.mpietrewicz.sp.ddd.annotations.domain.AggregateRoot;
import pl.mpietrewicz.sp.ddd.canonicalmodel.publishedlanguage.AggregateId;
import pl.mpietrewicz.sp.ddd.canonicalmodel.publishedlanguage.MonthlyBalance;
import pl.mpietrewicz.sp.ddd.canonicalmodel.publishedlanguage.RiskDefinition;
import pl.mpietrewicz.sp.ddd.sharedkernel.valueobject.PositiveAmount;
import pl.mpietrewicz.sp.ddd.support.infrastructure.repo.BaseAggregateRoot;

import javax.persistence.AttributeOverride;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@AggregateRoot
@Entity
@NoArgsConstructor
public class Allocation extends BaseAggregateRoot {

    @Embedded
    @AttributeOverride(name = "aggregateId", column = @Column(name = "contractId", nullable = false))
    private AggregateId contractId;

    @OneToMany(cascade = CascadeType.ALL)
    private List<Month> months = new ArrayList<>();

    public Allocation(AggregateId aggregateId, AggregateId contractId) {
        this.aggregateId = aggregateId;
        this.contractId = contractId;
    }

    public void add(List<MonthlyBalance> monthlyBalances, List<RiskDefinition> riskDefinitions) {
        for (MonthlyBalance monthlyBalance : monthlyBalances) {
            BigDecimal premium = monthlyBalance.getPremium();

            List<Risk> risks = new ArrayList<>();
            for (RiskDefinition riskDefinition : riskDefinitions) {
                PositiveAmount riskPremium = calculateRiskPremium(riskDefinition, premium);
                risks.add(new Risk(riskDefinition.getId(), riskDefinition.getName(), riskPremium));
            }
            months.add(new Month(monthlyBalance.getMonth(), risks));
        }
    }

    private PositiveAmount calculateRiskPremium(RiskDefinition risk, BigDecimal premium) {
        return risk.getPremiumDivisor().getQuotient(premium);
    }

}