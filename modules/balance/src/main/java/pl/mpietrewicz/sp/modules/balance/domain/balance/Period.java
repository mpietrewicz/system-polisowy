package pl.mpietrewicz.sp.modules.balance.domain.balance;

import lombok.Getter;
import lombok.NoArgsConstructor;
import pl.mpietrewicz.sp.ddd.canonicalmodel.publishedlanguage.MonthlyBalance;
import pl.mpietrewicz.sp.ddd.canonicalmodel.publishedlanguage.snapshot.premium.PremiumSnapshot;
import pl.mpietrewicz.sp.ddd.sharedkernel.valueobject.Amount;
import pl.mpietrewicz.sp.ddd.support.infrastructure.repo.BaseEntity;
import pl.mpietrewicz.sp.modules.balance.domain.balance.month.LastMonth;
import pl.mpietrewicz.sp.modules.balance.domain.balance.month.Month;
import pl.mpietrewicz.sp.modules.balance.domain.balance.month.MonthFactory;
import pl.mpietrewicz.sp.modules.balance.exceptions.RefundException;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Entity
@Getter
@NoArgsConstructor
public class Period extends BaseEntity implements PeriodProvider {

    private LocalDate start;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @JoinColumn(name = "period_id")
    private List<Month> months;

    private boolean isValid;

    private String info;

    public Period(LocalDate start, List<Month> months, boolean isValid, String info) {
        this.start = start;
        this.months = months;
        this.isValid = isValid;
        this.info = info;
    }

    public void pay(LastMonth start, Amount amount, PremiumSnapshot premiumSnapshot) {
        LastMonth month = start;
        Amount rest = amount;

        while (rest.isPositive()) {
            rest = month.pay(rest.castToPositive());
            addMonth(month);
            month = month.createNextMonth(premiumSnapshot);
        }
    }

    public void refund(LastMonth start, Amount amount) throws RefundException {
        LastMonth month = start;
        Amount rest = amount;

        while (rest.isPositive()) {
            rest = month.refund(rest.castToPositive());
            if (month.isUnpaid()) {
                removeMonth(month);
                month = getLastMonth()
                        .orElseThrow(() -> new RefundException("No enough amount to refund"));
            } else break;
        }
    }

    public Amount refund(LastMonth month) {
        Amount refunded = month.refund();
        if (month.isUnpaid()) {
            removeMonth(month);
        }
        return refunded;
    }

    public Amount refundUpTo(YearMonth yearMonth) {
        Amount refunded = Amount.ZERO;
        while (lastMonthBeforeOrEquals(yearMonth).isPresent()) {
            refunded = refunded.add(refund(lastMonthBeforeOrEquals(yearMonth).get()));
        }
        return refunded;
    }

    public LastMonth createFirstMonth(PremiumSnapshot premiumSnapshot) {
        if (getLastMonth().isPresent()) throw new IllegalStateException();

        YearMonth startMonth = YearMonth.from(start);
        return MonthFactory.create(startMonth, premiumSnapshot, false);
    }

    @Override
    public Period getCopy(String info) {
        Period period = new Period(start, new ArrayList<>(), true, info);
        List<Month> copiedMonths = months.stream()
                .map(Month::createCopy)
                .collect(Collectors.toList());
        period.months.addAll(copiedMonths);
        return period;
    }

    public void markAsInvalid() {
        this.isValid = false;
    }

    public List<MonthlyBalance> getMonthlyBalances() {
        return months.stream()
                .map(month -> new MonthlyBalance(month.getYearMonth(), month.getPremium(), month.getPaid()))
                .collect(Collectors.toList());
    }

    public List<YearMonth> getRenewalMonths() {
        return months.stream()
                .filter(Month::isRenewal)
                .map(Month::getYearMonth)
                .collect(Collectors.toList());
    }

    public Optional<LastMonth> getLastMonth() {
        return months.stream()
                .max(Month::compareAscending)
                .map(LastMonth.class::cast);
    }

    public boolean has(YearMonth month) {
        return months.stream()
                .anyMatch(m -> m.getYearMonth().equals(month));
    }

    public YearMonth getLastPaidYearMonth() {
        return months.stream()
                .filter(Month::isPaid)
                .max(Month::compareAscending)
                .map(Month::getYearMonth)
                .orElse(YearMonth.from(start).minusMonths(1));
    }

    public boolean isValid() {
        return isValid;
    }

    private void addMonth(LastMonth month) {
        if (!months.contains(month)) {
            months.add((Month) month);
        }
    }

    private void removeMonth(LastMonth month) {
        months.remove(month);
    }

    private Optional<LastMonth> lastMonthBeforeOrEquals(YearMonth yearMonth) {
        return months.stream()
                .max(Month::compareAscending)
                .filter(lastMonth -> lastMonth.getYearMonth().compareTo(yearMonth) >= 0)
                .map(LastMonth.class::cast);
    }

}