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
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@AggregateRoot
@Entity
@NoArgsConstructor
public class Allocation extends BaseAggregateRoot {

    @Embedded
    @AttributeOverride(name = "aggregateId", column = @Column(name = "contractId", nullable = false))
    private AggregateId contractId;

    @OneToMany(cascade = CascadeType.ALL)
    private List<Month> months = new ArrayList<>();

    public Allocation(AggregateId contractId) {
        this.contractId = contractId;
    }

    public Allocation(AggregateId contractId, List<Month> months) {
        this.contractId = contractId;
        this.months = months;
    }

    public void update(List<MonthlyBalance> monthlyBalances, List<RiskDefinition> riskDefinitions) {
        for (MonthlyBalance monthlyBalance : monthlyBalances) {
            Optional<Month> currentMonth = getCurrentMonth(monthlyBalance);
            if (currentMonth.isEmpty()) {
                Month month = AllocationFactory.createMonth(monthlyBalance, riskDefinitions);
                this.months.add(month);
            } else {
                currentMonth.get().correct(monthlyBalance, riskDefinitions);
            }
        }
    }

    public PositiveAmount getAmount() {
        return months.stream()
                .map(Month::getAmount)
                .reduce(PositiveAmount::add)
                .orElseThrow();
    }

    private Optional<Month> getCurrentMonth(MonthlyBalance monthlyBalance) {
        return months.stream()
                .filter(month -> month.isAppliesTo(monthlyBalance))
                .findAny();
    }

}