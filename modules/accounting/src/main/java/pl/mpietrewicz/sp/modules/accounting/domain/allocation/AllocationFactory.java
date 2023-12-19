package pl.mpietrewicz.sp.modules.accounting.domain.allocation;

import pl.mpietrewicz.sp.ddd.annotations.domain.DomainFactory;
import pl.mpietrewicz.sp.ddd.canonicalmodel.publishedlanguage.AggregateId;
import pl.mpietrewicz.sp.ddd.canonicalmodel.publishedlanguage.MonthlyBalance;
import pl.mpietrewicz.sp.ddd.canonicalmodel.publishedlanguage.RiskDefinition;
import pl.mpietrewicz.sp.ddd.canonicalmodel.publishedlanguage.snapshot.ContractData;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@DomainFactory
public class AllocationFactory {

    public static Allocation create(ContractData contractData, List<MonthlyBalance> monthlyBalances, List<RiskDefinition> riskDefinitions) {
        List<Month> months = createMonths(monthlyBalances, riskDefinitions);
        return new Allocation(contractData, months);
    }

    private static List<Month> createMonths(List<MonthlyBalance> monthlyBalances, List<RiskDefinition> riskDefinitions) {
        return monthlyBalances.stream()
                .map(monthlyBalance -> createMonth(monthlyBalance, riskDefinitions))
                .collect(Collectors.toList());
    }

    public static Month createMonth(MonthlyBalance monthlyBalance, List<RiskDefinition> riskDefinitions) {
        List<Component> components = createComponents(monthlyBalance.getComponentPremiums(), riskDefinitions);
        return new Month(monthlyBalance.getMonth(), components);
    }

    private static List<Component> createComponents(Map<AggregateId, BigDecimal> componentPremiums, List<RiskDefinition> riskDefinitions) {
        List<Component> components = new ArrayList<>();
        for (Map.Entry<AggregateId, BigDecimal> componentPremium : componentPremiums.entrySet()) {
            Component component = createComponent(componentPremium, riskDefinitions);
            components.add(component);
        }
        return components;
    }

    public static Component createComponent(Map.Entry<AggregateId, BigDecimal> componentPremium, List<RiskDefinition> riskDefinitions) {
        AggregateId componentId = componentPremium.getKey();
        BigDecimal premium = componentPremium.getValue();
        List<Risk> risks = createRisks(premium, riskDefinitions);
        return new Component(componentId, risks);
    }

    private static List<Risk> createRisks(BigDecimal premium, List<RiskDefinition> riskDefinitions) {
        List<Risk> risks = new ArrayList<>();
        for (RiskDefinition riskDefinition : riskDefinitions) {
            Risk risk = createRisk(premium, riskDefinition);
            risks.add(risk);
        }
        return risks;
    }

    public static Risk createRisk(BigDecimal premium, RiskDefinition riskDefinition) {
        BigDecimal riskPremium = calculateRiskPremium(premium, riskDefinition);
        return new Risk(riskDefinition.getId(), riskPremium);
    }

    private static BigDecimal calculateRiskPremium(BigDecimal premium, RiskDefinition risk) {
        return risk.getPremiumDivisor().getQuotient(premium);
    }

}