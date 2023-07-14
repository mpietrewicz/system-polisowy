package pl.mpietrewicz.sp.modules.oldbalance.domain.balance;

import lombok.Getter;
import pl.mpietrewicz.sp.modules.oldbalance.domain.balance.changeduestrategy.AddDueChangeStrategy;
import pl.mpietrewicz.sp.modules.oldbalance.domain.balance.changeduestrategy.DeleteDueChangeStrategy;
import pl.mpietrewicz.sp.modules.oldbalance.domain.balance.changeduestrategy.UpdateDueChangeStrategy;
import pl.mpietrewicz.sp.modules.oldbalance.domain.balance.period.Period;
import pl.mpietrewicz.sp.ddd.annotations.domain.AggregateRoot;
import pl.mpietrewicz.sp.ddd.canonicalmodel.publishedlanguage.AggregateId;
import pl.mpietrewicz.sp.ddd.canonicalmodel.publishedlanguage.snapshot.ComponentData;
import pl.mpietrewicz.sp.ddd.canonicalmodel.publishedlanguage.snapshot.ContractData;
import pl.mpietrewicz.sp.ddd.canonicalmodel.publishedlanguage.snapshot.PaymentData;
import pl.mpietrewicz.sp.ddd.canonicalmodel.publishedlanguage.snapshot.RefundData;
import pl.mpietrewicz.sp.ddd.support.domain.BaseAggregateRoot;

import javax.persistence.Embedded;
import javax.persistence.Entity;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.Optional;

import static java.math.BigDecimal.ZERO;
import static pl.mpietrewicz.sp.modules.oldbalance.domain.balance.policy.operationcalculation.PaymentCalculationPolicyEnum.SIMPLE;

@AggregateRoot
@Entity
public class Balance extends BaseAggregateRoot { // todo: sprawdzić czy kod zachowuje zasady SOLID i prawo demeter!
    // todo: odpowiedzialność za wyznaczone OO powinien przyjmować balance na podstawie dpo, lub wznowienia
    private ContractData contractData;

    @Embedded
    @Getter
    private Periods periods;

    @Embedded
    private Operations operations;

    public Balance() {
    }

    public Balance(AggregateId aggregateId, ContractData contractData, Periods periods, Operations operations) {
        this.aggregateId = aggregateId;
        this.contractData = contractData;
        this.periods = periods;
        this.operations = operations;
    }

    public void addPayment(PaymentData paymentData) {
        operations.addPayment(paymentData, periods, SIMPLE); // todo: do wyniesienia wyżej
    }

    public void addRefund(RefundData refundData) {
        operations.addRefund(refundData, periods, SIMPLE); // todo: do wyniesienia wyżej
    }

    public void addComponent(ComponentData componentData, BigDecimal premium) {
        YearMonth componentStart = YearMonth.from(componentData.getStartDate());
        Optional<Period> componentStartPeriod = periods.getAt(componentStart);
        AddDueChangeStrategy addDueStrategy = new AddDueChangeStrategy(componentData, premium);

        componentStartPeriod.ifPresent(
                period -> updateBalanceAfter(addDueStrategy, period)
        );
    }

    public void updatePremium(ComponentData componentData, LocalDate since, BigDecimal premium) {
        YearMonth changePremiumStart = YearMonth.from(since);
        Optional<Period> changePremiumStartPeriod = periods.getAt(changePremiumStart);
        UpdateDueChangeStrategy updateDueStrategy = new UpdateDueChangeStrategy(componentData, premium);

        changePremiumStartPeriod.ifPresent(
                period -> updateBalanceAfter(updateDueStrategy, period)
        );
    }

    public void terminateComponent(ComponentData componentData, LocalDate terminatedDate) {
        YearMonth terminateMonth = YearMonth.from(terminatedDate);
        YearMonth monthAfterTermination = terminateMonth.plusMonths(1);
        Optional<Period> periodAfterTermination = periods.getAt(monthAfterTermination);
        DeleteDueChangeStrategy deleteDueStrategy = new DeleteDueChangeStrategy(componentData);

        periodAfterTermination.ifPresent(
                period -> updateBalanceAfter(deleteDueStrategy, period)
        );
    }

    public void addPeriods(int months) {
        for (int i = 0; i < months; i++) {
            periods.add();
        }
    }

    private void updateBalanceAfter(DueChangeStrategy dueChangeStrategy, Period startPeriod) {
        startPeriod.updatePremiumDue(dueChangeStrategy);
        updateBalanceSince(startPeriod);
    }

    private void updateBalanceSince(Period period) {
        operations.recalculate(period.getMonth(), periods);
    }

    public YearMonth getPaidTo() {
        return periods.getLastPaidPeriod()
                .map(Period::getMonth)
                .orElse(periods.getFirstPeriod().getMonth().minusMonths(1));
    }

    public BigDecimal getUnderpayment() {
        if (periods.getLastPeriod().isNotPaid()) {
            return periods.getNotPaid().stream()
                    .filter(period -> period.getMonth().compareTo(getPaidTo()) > 0)
                    .map(period -> period.getUnderpayment().compareTo(ZERO) > 0
                            ? period.getUnderpayment()
                            : period.getPremiumDue())
                    .reduce(BigDecimal::add)
                    .orElse(ZERO);
        } else {
            return ZERO;
        }
    }

    public BigDecimal getOverpayment() {
        return periods.getLastPeriod().getOverpayment();
    }

    public YearMonth getLastPerioMonth() {
        return periods.getLastPeriod().getMonth();
    }

}