package pl.mpietrewicz.sp.modules.accounting.domain.allocation;

import lombok.NoArgsConstructor;
import pl.mpietrewicz.sp.ddd.annotations.domain.ValueObject;
import pl.mpietrewicz.sp.ddd.canonicalmodel.publishedlanguage.AggregateId;
import pl.mpietrewicz.sp.ddd.canonicalmodel.publishedlanguage.MonthlyBalance;
import pl.mpietrewicz.sp.ddd.canonicalmodel.publishedlanguage.RiskDefinition;
import pl.mpietrewicz.sp.ddd.sharedkernel.valueobject.PositiveAmount;
import pl.mpietrewicz.sp.ddd.support.infrastructure.repo.BaseEntity;

import javax.persistence.Entity;
import javax.persistence.OneToMany;
import java.time.YearMonth;
import java.util.List;
import java.util.Optional;

import static javax.persistence.CascadeType.ALL;

@ValueObject
@Entity(name = "month_allloc")
@NoArgsConstructor
public class Month extends BaseEntity {

    private YearMonth month;

    @OneToMany(cascade = ALL)
    private List<Component> components;

    public Month(YearMonth month, List<Component> components) {
        this.month = month;
        this.components = components;
    }

    public void correct(MonthlyBalance monthlyBalance, List<RiskDefinition> riskDefinitions) {
//        for (Map.Entry<AggregateId, Amount> componentPremium : monthlyBalance.getComponentPremiums().entrySet()) {
//            Optional<Component> currentComponent = getCurrentComponent(componentPremium.getKey());
//            if (currentComponent.isEmpty()) {
//                Component component = AllocationFactory.createComponent(componentPremium, riskDefinitions);
//                this.components.add(component);
//            } else {
//                currentComponent.get().correct(componentPremium, riskDefinitions);
//            }
//        }
    }

    private Optional<Component> getCurrentComponent(AggregateId componentId) {
        return this.components.stream()
                .filter(component -> component.isAppliesTo(componentId))
                .findAny();
    }

    public PositiveAmount getAmount() {
        return components.stream()
                .map(Component::getAmount)
                .reduce(PositiveAmount::add)
                .orElseThrow();
    }

    public boolean isAppliesTo(MonthlyBalance monthlyBalance) {
        return month.equals(monthlyBalance.getMonth());
    }

}