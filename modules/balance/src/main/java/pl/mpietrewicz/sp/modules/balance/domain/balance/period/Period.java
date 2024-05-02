package pl.mpietrewicz.sp.modules.balance.domain.balance.period;

import lombok.Getter;
import lombok.NoArgsConstructor;
import pl.mpietrewicz.sp.ddd.canonicalmodel.publishedlanguage.MonthlyBalance;
import pl.mpietrewicz.sp.ddd.canonicalmodel.publishedlanguage.snapshot.premium.PremiumSnapshot;
import pl.mpietrewicz.sp.ddd.sharedkernel.valueobject.Amount;
import pl.mpietrewicz.sp.ddd.sharedkernel.valueobject.PositiveAmount;
import pl.mpietrewicz.sp.ddd.sharedkernel.valueobject.ZeroAmount;
import pl.mpietrewicz.sp.modules.balance.domain.balance.month.LastMonth;
import pl.mpietrewicz.sp.modules.balance.domain.balance.month.Month;
import pl.mpietrewicz.sp.modules.balance.domain.balance.month.MonthFactory;
import pl.mpietrewicz.sp.modules.balance.exceptions.RefundException;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Getter
@NoArgsConstructor
public class Period implements PeriodProvider {

    private LocalDate start;

    private List<Month> months;

    private String info;

    public Period(LocalDate start, List<Month> months, String info) {
        this.start = start;
        this.months = months;
        this.info = info;
    }

    public void pay(LastMonth start, PositiveAmount payment, PremiumSnapshot premiumSnapshot) {
        LastMonth month = start;
        Amount rest = payment;

        while (rest.isPositive()) {
            rest = month.pay((PositiveAmount) rest);
            addMonth(month);
            month = month.createNextMonth(premiumSnapshot);
        }
    }

    public void refundAmount(LastMonth start, PositiveAmount refund) throws RefundException {
        LastMonth month = start;
        Amount rest = refund;

        while (rest.isPositive()) {
            rest = month.refund((PositiveAmount) rest);
            remove(month);
            month = getLastMonth()
                    .orElseThrow(() -> new RefundException("No enough amount to refund"));
        }
    }

    public Amount refundUpTo(YearMonth yearMonth) {
        Optional<LastMonth> lastMonth = lastMonthBeforeOrEquals(yearMonth);
        Map<LastMonth, PositiveAmount> refunds = new HashMap<>();

        while (lastMonth.isPresent()) {
            Amount refunded = refundMonth(lastMonth.get());
            if (refunded.isPositive()) {
                refunds.put(lastMonth.get(), (PositiveAmount) refunded);
            }
            lastMonth = lastMonthBeforeOrEquals(yearMonth);
        }

        return refunds.isEmpty()
                ? new ZeroAmount()
                : refunds.values().stream()
                    .reduce(PositiveAmount::add)
                    .orElseThrow();
    }

    public Amount refundMonth(LastMonth month) {
        Amount refunded = month.refund();
        if (month.isUnpaid()) {
            remove(month);
        }
        return refunded;
    }

    public LastMonth createFirstMonth(PremiumSnapshot premiumSnapshot) {
        if (getLastMonth().isPresent()) throw new IllegalStateException();

        YearMonth startMonth = YearMonth.from(start);
        return MonthFactory.create(startMonth, premiumSnapshot, false);
    }

    public Period getCopy(String info) {
        List<Month> copiedMonths = getValidMonths().stream()
                .map(Month::createCopy)
                .collect(Collectors.toList());
        return new Period(start, copiedMonths, info);
    }

    public List<Month> getValidMonths() {
        return months.stream()
                .filter(Month::isValid)
                .collect(Collectors.toList());
    }

    public List<MonthlyBalance> getMonthlyBalances() {
        return getValidMonths().stream()
                .map(month -> new MonthlyBalance(month.getYearMonth(), month.getPremium(), month.getPaid().getValue()))
                .collect(Collectors.toList());
    }

    public List<YearMonth> getRenewalMonths() {
        return getValidMonths().stream()
                .filter(Month::isRenewal)
                .map(Month::getYearMonth)
                .collect(Collectors.toList());
    }

    public Optional<LastMonth> getLastMonth() {
        return getValidMonths().stream()
                .max(Month::compareAscending)
                .map(LastMonth.class::cast);
    }

    public boolean has(YearMonth month) {
        return getValidMonths().stream()
                .anyMatch(m -> m.getYearMonth().equals(month));
    }

    public YearMonth getLastPaidYearMonth() {
        return getLastPaidMonth().map(Month::getYearMonth)
                .orElse(YearMonth.from(start).minusMonths(1));
    }

    public BigDecimal getExcess() {
        Optional<Month> lastPaidMonth = getLastPaidMonth();
        return getValidMonths().stream()
                .filter(month -> lastPaidMonth.filter(month::isAfter).isPresent())
                .map(Month::getPaid)
                .map(Amount::getValue)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private Optional<Month> getLastPaidMonth() {
        return getValidMonths().stream()
                .filter(Month::isValid)
                .filter(Month::isPaid)
                .max(Month::compareAscending);
    }

    private void addMonth(LastMonth month) {
        if (!months.contains(month)) {
            months.add((Month) month);
        }
    }

    private void remove(LastMonth month) {
        months.remove(month);
    }

    private Optional<LastMonth> lastMonthBeforeOrEquals(YearMonth yearMonth) {
        return getValidMonths().stream()
                .max(Month::compareAscending)
                .filter(lastMonth -> lastMonth.getYearMonth().compareTo(yearMonth) >= 0)
                .map(LastMonth.class::cast);
    }

}