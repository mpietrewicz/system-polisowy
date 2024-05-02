package pl.mpietrewicz.sp.modules.accounting.domain.allocation;

import pl.mpietrewicz.sp.ddd.annotations.domain.DomainFactory;
import pl.mpietrewicz.sp.ddd.canonicalmodel.publishedlanguage.AggregateId;
import pl.mpietrewicz.sp.ddd.canonicalmodel.publishedlanguage.MonthlyBalance;
import pl.mpietrewicz.sp.ddd.canonicalmodel.publishedlanguage.RiskDefinition;
import pl.mpietrewicz.sp.ddd.sharedkernel.valueobject.PositiveAmount;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@DomainFactory
public class AllocationFactory {

    public static Allocation create(AggregateId contractId, List<MonthlyBalance> monthlyBalances, List<RiskDefinition> riskDefinitions) {
        List<Month> months = createMonths(monthlyBalances, riskDefinitions);
        return new Allocation(contractId, months);
    }

    private static List<Month> createMonths(List<MonthlyBalance> monthlyBalances, List<RiskDefinition> riskDefinitions) {
        return monthlyBalances.stream()
                .map(monthlyBalance -> createMonth(monthlyBalance, riskDefinitions))
                .collect(Collectors.toList());
    }

    public static Month createMonth(MonthlyBalance monthlyBalance, List<RiskDefinition> riskDefinitions) {
//        List<Component> components = createComponents(monthlyBalance.getComponentPremiums(), riskDefinitions);
//        return new Month(monthlyBalance.getMonth(), components);
        return null;
    }

    private static List<Component> createComponents(Map<AggregateId, PositiveAmount> componentPremiums, List<RiskDefinition> riskDefinitions) {
        List<Component> components = new ArrayList<>();
        for (Map.Entry<AggregateId, PositiveAmount> componentPremium : componentPremiums.entrySet()) {
            Component component = createComponent(componentPremium, riskDefinitions);
            components.add(component);
        }
        return components;
    }

    public static Component createComponent(Map.Entry<AggregateId, PositiveAmount> componentPremium, List<RiskDefinition> riskDefinitions) {
        AggregateId componentId = componentPremium.getKey();
        PositiveAmount premium = componentPremium.getValue();
        List<Risk> risks = createRisks(premium, riskDefinitions);
        return new Component(componentId, risks);
    }

    private static List<Risk> createRisks(PositiveAmount premium, List<RiskDefinition> riskDefinitions) {
        List<Risk> risks = new ArrayList<>();
        for (RiskDefinition riskDefinition : riskDefinitions) {
            Risk risk = createRisk(premium, riskDefinition);
            risks.add(risk);
        }
        return risks;
    }

    public static Risk createRisk(PositiveAmount premium, RiskDefinition riskDefinition) {
        PositiveAmount riskAmount = calculateRiskPremium(premium, riskDefinition);
        return new Risk(riskDefinition.getId(), riskAmount);
    }

    private static PositiveAmount calculateRiskPremium(PositiveAmount premium, RiskDefinition risk) {
        return risk.getPremiumDivisor().getQuotient(premium);
    }

}