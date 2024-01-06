package pl.mpietrewicz.sp.modules.balance.domain.balance.paymentpolicy;

import lombok.NoArgsConstructor;
import pl.mpietrewicz.sp.ddd.annotations.domain.DomainEntity;
import pl.mpietrewicz.sp.ddd.canonicalmodel.publishedlanguage.MonthlyBalance;
import pl.mpietrewicz.sp.ddd.sharedkernel.Amount;
import pl.mpietrewicz.sp.modules.balance.domain.balance.Premium;
import pl.mpietrewicz.sp.modules.balance.domain.balance.month.ComponentPremium;
import pl.mpietrewicz.sp.modules.balance.domain.balance.month.Month;
import pl.mpietrewicz.sp.modules.balance.domain.balance.operation.PaymentData;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static java.util.function.Predicate.not;
import static pl.mpietrewicz.sp.DateUtils.getMonthsBetween;
import static pl.mpietrewicz.sp.ddd.sharedkernel.Amount.ZERO;
import static pl.mpietrewicz.sp.modules.balance.domain.balance.month.MonthStatus.UNPAID;

@DomainEntity
@Embeddable
@NoArgsConstructor
public class Period {

    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "operation_id")
    private List<Month> months;

    public Period(List<Month> months) {
        this.months = months;
    }

    public static Period init(LocalDate from, Premium premium, int grace) {
        List<Month> months = new ArrayList<>();
        YearMonth yearMonth = YearMonth.from(from);

        for (int i = 0; i < grace; i++) {
            Month month = new Month(yearMonth.plusMonths(i), UNPAID, ZERO, ZERO, premium.getComponentPremiums());
            months.add(month);
        }
        return new Period(months);
    }

    public void tryPay(PaymentPolicy paymentPolicy, PaymentData paymentData) {
        Month monthToPay = paymentPolicy.getMonthToPay(this, paymentData);
        Amount payment = paymentData.getAmount();

        List<Month> miesiaceDoRozsmarowania = getMonthsStarting(monthToPay);
        rozsmarujWplatePoMiesiacach(payment, miesiaceDoRozsmarowania);
    }

    public void rozsmarujWplatePoMiesiacach(Amount payment, List<Month> months) { // todo: zmienić nazwę
        for (Month month : months) {
            if (payment.isPositive()) {
                payment = month.pay(payment.castToPositive(), getNextMonth(month)); // todo: tutaj powinienem przekazywać jaka jest aktualna składka za miesiac
            }
        }
        if (payment.isPositive()) throw new IllegalStateException();
    }

    public void tryRefund(Amount refund) {
        rozsmarujZwrotPoMiesiacach(refund, getDescendingMonths());
    }

    public Amount tryRefundUpTo(List<Month> months) {
        return months.stream()
                .sorted(Month::compareDescending)
                .map(this::tryRefund)
                .reduce(ZERO, Amount::add);
    }

    private Amount tryRefund(Month month) {
        Optional<Month> nextMonth = getNextMonth(month);

        if (nextMonth.isEmpty() || nextMonth.get().isNotPaid()) {
            return month.refund();
        } else {
            throw new IllegalStateException("Nie można zwrócić gdy kolejny miesiac jest opłacony");
        }
    }

    private void rozsmarujZwrotPoMiesiacach(Amount refund, List<Month> months) {  // todo: zmienić nazwę
        for (Month month : months) {
            if (refund.isPositive()) {
                refund = month.refund(refund.castToPositive(), getPreviousMonth(month));
            }
        }
        if (refund.isPositive()) throw new IllegalStateException();
    }

    public void includeGracePeriod(Premium premium, int grace) {
        int unpaidMonths = getMonthsBetween(getLastPaidYearMonth(), getLastYearMonth());

        if (unpaidMonths < grace) {
            extendPeriodToGrace(unpaidMonths, premium, grace);
        } else {
            reducePeriodToGrace(unpaidMonths, grace);
        }
    }

    public Period createCopy() {
        List<Month> copiedMonths = months.stream()
                .map(Month::createCopy)
                .collect(Collectors.toList());
        return new Period(copiedMonths);
    }

    public List<MonthlyBalance> getMonthlyBalances() {
        return months.stream()
                .map(month -> MonthlyBalance.builder()
                        .month(month.getYearMonth())
                        .componentPremiums(month.getPremiumComponents())
                        .isPaid(month.isPaid())
                        .build())
                .collect(Collectors.toList());
    }

    public YearMonth getLastMonthOfLiability() {
        return getLastMonth().getYearMonth();
    }

    protected void addNewMonth(Month newMonth) {
        if (months.stream().anyMatch(month -> month.getYearMonth().equals(newMonth.getYearMonth()))) {
            throw new IllegalStateException();
        }
        months.add(newMonth);
    }

    protected Month getFirstMonth() {
        return months.stream()
                .min(Month::compareAscending)
                .orElseThrow();
    }

    protected Optional<Month> getLastPaidMonth() {
        return months.stream()
                .filter(Month::isPaid)
                .max(Month::compareAscending);
    }

    public Optional<Month> getNextMonth(Month month) {
        return months.stream()
                .filter(m -> m.isAfter(month))
                .min(Month::compareAscending);
    }

    protected Optional<Month> getMonthOf(LocalDate date) {
        return months.stream()
                .filter(month -> month.getYearMonth().equals(YearMonth.from(date)))
                .findAny();
    }

    private void deleteMonths(YearMonth from) {
        List<Month> descendingMonths = getDescendingMonthsStarting(from);
        descendingMonths.forEach(this::deleteMonth);
    }

    private void deleteMonth(Month month) {
        getOptionalLastMonth()
                .filter(last -> last.equals(month))
                .filter(Month::canBeDeleted)
                .ifPresentOrElse(removable -> months.remove(removable), () -> {
                    throw new IllegalArgumentException("Nie można usunąć danego miesiąca");
                });
    }

    private void extendPeriodToGrace(int unpaidMonths, Premium premium, int grace) {
        int limit = grace - unpaidMonths;
        while (limit > 0) {
            addNextMonth(premium);
            limit = getLastMonth().isNotPaid() || getLastMonth().isPartlyPaid() ? limit - 1 : limit;
        }
    }

    private void reducePeriodToGrace(int unpaidMonths, int grace) {
        int monthsToDelete = unpaidMonths - grace - 1;
        deleteMonths(getLastYearMonth().minusMonths(monthsToDelete));
    }

    private void addNextMonth(Premium premium) {
        List<ComponentPremium> componentPremiums = premium.getComponentPremiums();
        Month month = getLastMonth().createNextMonth(componentPremiums);
        months.add(month);
    }

    private Month getLastMonth() {
        return months.stream()
                .max(Month::compareAscending)
                .orElseThrow();
    }

    private Optional<Month> getOptionalLastMonth() {
        return months.stream()
                .max(Month::compareAscending);
    }

    private YearMonth getLastYearMonth() {
        return getLastMonth().getYearMonth();
    }

    private YearMonth getLastPaidYearMonth() {
        return months.stream()
                .filter(Month::isPaid)
                .max(Month::compareAscending)
                .map(Month::getYearMonth)
                .orElse(getFirstMonth().getYearMonth().minusMonths(1));
    }

    private Optional<Month> getPreviousMonth(Month month) {
        return months.stream()
                .filter(m -> m.isBefore(month))
                .max(Month::compareAscending);
    }

    private List<Month> getDescendingMonths() {
        return months.stream()
                .sorted(Month::compareDescending)
                .collect(Collectors.toList());
    }

    private List<Month> getMonthsStarting(Month from) {
        return months.stream()
                .filter(not(month -> month.isBefore(from)))
                .sorted(Month::compareAscending)
                .collect(Collectors.toList());
    }

    public List<Month> getDescendingMonthsStarting(YearMonth yearMonth) {
        return months.stream()
                .filter(not(month -> month.getYearMonth().isBefore(yearMonth)))
                .sorted(Month::compareDescending)
                .collect(Collectors.toList());
    }

}