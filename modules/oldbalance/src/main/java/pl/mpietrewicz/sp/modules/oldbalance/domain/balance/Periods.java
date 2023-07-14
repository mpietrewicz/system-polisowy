package pl.mpietrewicz.sp.modules.oldbalance.domain.balance;

import pl.mpietrewicz.sp.modules.oldbalance.domain.balance.operation.Operation;
import pl.mpietrewicz.sp.modules.oldbalance.domain.balance.operation.Refund;
import pl.mpietrewicz.sp.modules.oldbalance.domain.balance.period.Period;
import pl.mpietrewicz.sp.modules.oldbalance.domain.balance.policy.PeriodCoverPolicy;
import pl.mpietrewicz.sp.ddd.annotations.domain.ValueObject;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import java.time.YearMonth;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static pl.mpietrewicz.sp.modules.oldbalance.domain.balance.period.Period.ASCENDING;
import static pl.mpietrewicz.sp.modules.oldbalance.domain.balance.period.Period.DESCENDING;

@ValueObject
@Embeddable
public class Periods {

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "balance_id")
    private List<Period> periods;

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "period_cover_policy_id")
    private PeriodCoverPolicy periodCoverPolicy;

    public Periods() {
    }

    public Periods(List<Period> periods, PeriodCoverPolicy periodCoverPolicy) {
        this.periods = periods;
        this.periodCoverPolicy = periodCoverPolicy;
    }

    public Period getFirstPeriodToPay(Operation operation) {
        return periodCoverPolicy.getFirstPeriodToPay(this, operation);
    }

    public Period getLastPeriodToRefund(Refund refund) {
        return periodCoverPolicy.getLastPeriodToRefund(this, refund);
    }

    public void eraseState() {
        periods.forEach(Period::eraseState);
    }

    public void eraseStateAfter(YearMonth month) {
        periods.stream()
                .filter(period -> period.isAfter(month))
                .forEach(Period::eraseState);
    }

    public void add() {
        Period last = getLastPeriod();
        Period next = last.tryCreateNextPeriod();
        this.periods.add(next);
    }

    public List<Period> getPeriods() {
        return periods;
    }

    public Period getFirstPeriod() {
        return periods.stream()
                .min(ASCENDING)
                .orElseThrow();
    }

    public List<Period> getAscPeriodsAfter(YearMonth month) {
        return periods.stream()
                .filter(period -> period.getMonth().compareTo(month) > 0)
                .sorted(ASCENDING)
                .collect(Collectors.toList());
    }

    public List<Period> getDescPeriodsBefore(YearMonth month) {
        return periods.stream()
                .filter(period -> period.getMonth().compareTo(month) < 0)
                .sorted(DESCENDING)
                .collect(Collectors.toList());
    }

    public Optional<Period> getAt(YearMonth month) {
        return periods.stream()
                .filter(period -> period.getMonth().compareTo(month) == 0)
                .findAny();
    }

    public Period getLastPeriod() {
        return periods.stream()
                .min(DESCENDING)
                .orElseThrow();
    }

    public Optional<Period> getLastPaidPeriod() {
        return periods.stream()
                .filter(Period::isPaid)
                .max(ASCENDING);
    }



    public List<Period> getCovered() {
        return periods.stream()
                .filter(Period::isCovered)
                .collect(Collectors.toList());
    }

    public List<Period> getNotPaid() {
        return periods.stream()
                .filter(Period::isNotPaid)
                .collect(Collectors.toList());
    }

    public List<Period> getPaid() {
        return periods.stream()
                .filter(Period::isPaid)
                .collect(Collectors.toList());
    }

    public List<Period> getSince(Period period) {
        return periods.stream()
                .filter(p -> p.isAt(period.getMonth())
                        || p.isAfter(period.getMonth()))
                .collect(Collectors.toList());
    }

    public List<Period> getUntil(Period period) {
        return periods.stream()
                .filter(p -> p.isBefore(period.getMonth())
                        || p.isAt(period.getMonth()))
                .collect(Collectors.toList());
    }

}