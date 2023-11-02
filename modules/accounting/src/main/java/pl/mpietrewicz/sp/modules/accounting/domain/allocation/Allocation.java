package pl.mpietrewicz.sp.modules.accounting.domain.allocation;

import lombok.NoArgsConstructor;
import pl.mpietrewicz.sp.ddd.annotations.domain.AggregateRoot;
import pl.mpietrewicz.sp.ddd.canonicalmodel.publishedlanguage.MonthlyBalance;
import pl.mpietrewicz.sp.ddd.canonicalmodel.publishedlanguage.RiskDefinition;
import pl.mpietrewicz.sp.ddd.canonicalmodel.publishedlanguage.snapshot.ContractData;
import pl.mpietrewicz.sp.modules.accounting.ddd.support.domain.BaseAggregateRoot;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static java.math.BigDecimal.ZERO;

@AggregateRoot
@Entity
@NoArgsConstructor
public class Allocation extends BaseAggregateRoot {

    private ContractData contractData;

    @OneToMany(cascade = CascadeType.ALL)
    private List<Month> months = new ArrayList<>();

    public Allocation(ContractData contractData) {
        this.contractData = contractData;
    }

    public Allocation(ContractData contractData, List<Month> months) {
        this.contractData = contractData;
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

    public BigDecimal getAmount() {
        return months.stream()
                .map(Month::getAmount)
                .reduce(ZERO, BigDecimal::add);
    }

    private Optional<Month> getCurrentMonth(MonthlyBalance monthlyBalance) {
        return months.stream()
                .filter(month -> month.isAppliesTo(monthlyBalance))
                .findAny();
    }
}