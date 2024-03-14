package pl.mpietrewicz.sp.modules.balance.domain.balance;

import lombok.Getter;
import pl.mpietrewicz.sp.ddd.canonicalmodel.publishedlanguage.MonthlyBalance;
import pl.mpietrewicz.sp.ddd.canonicalmodel.publishedlanguage.snapshot.premium.PremiumSnapshot;
import pl.mpietrewicz.sp.ddd.sharedkernel.Amount;
import pl.mpietrewicz.sp.ddd.sharedkernel.PositiveAmount;
import pl.mpietrewicz.sp.modules.balance.domain.balance.month.Month;
import pl.mpietrewicz.sp.modules.balance.exceptions.PaymentException;
import pl.mpietrewicz.sp.modules.balance.exceptions.RefundException;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static pl.mpietrewicz.sp.ddd.sharedkernel.Amount.ZERO;

@Getter
public class Period implements PeriodService, PeriodProvider { // todo: przejrzeć do refaktoru

    private Long id;

    private final List<Month> months;

    private final LocalDate start;

    private boolean isValid;

    private final String info;

    public Period(LocalDate start, List<Month> months, boolean isValid, String info) {
        this.start = start;
        this.months = months;
        this.isValid = isValid;
        this.info = info;
    }

    public Period(Long id, LocalDate start, List<Month> months, boolean isValid, String info) {
        this.id = id;
        this.start = start;
        this.months = months;
        this.isValid = isValid;
        this.info = info;
    }

    public void tryPay(MonthToPay monthToPay, PremiumSnapshot premiumSnapshot) throws PaymentException {
        Month month = monthToPay.getMonth();
        Amount amount = monthToPay.getAmount();

        do {
            amount = month.pay(amount.castToPositive());
            if (month.isPaid() && amount.isPositive()) {
                month = createNextMonth(month, premiumSnapshot);
            }
        } while (amount.isPositive());
    }

    public void tryRefund(Amount refund) throws RefundException {
        if (refund.isPositive()) {
            refund = refund.castToPositive();
        } else {
            return;
        }
        Month month = getLastMonthWithPayment()
                .orElseThrow(() -> new RefundException("No enough amount to refund"));

        do {
            refund = month.refund(refund.castToPositive());
            if (!month.isPaid()) {
                months.remove(month);
                month = getPreviousMonth(month)
                        .orElseThrow(() -> new RefundException("No enough amount to refund"));
            }
        } while (refund.isPositive());
    }

    public Amount tryRefundUpTo(YearMonth yearMonth) {
        return months.stream()
                .filter(month -> month.getYearMonth().compareTo(yearMonth) >= 0)
                .sorted(Month::compareDescending)
                .map(month -> {
                    Amount refunded = month.refund();
                    months.remove(month);
                    return refunded;
                }).reduce(ZERO, Amount::add);
    }

    public Amount refundUnderpayment() {
        return getLastMonth()
                .map(month -> {
                    Amount refunded = month.refund();
                    months.remove(month);
                    return refunded;
                }).orElse(ZERO);
    }

    private Month createNextMonth(Month month, PremiumSnapshot premiumSnapshot) {
        YearMonth yearMonth = month.getYearMonth().plusMonths(1);
        PositiveAmount premium = premiumSnapshot.getAmountAt(yearMonth.atDay(1));
        Month nextMonth = createMonth(yearMonth, premium);
        addNewMonth(nextMonth);
        return nextMonth;
    }

    public Period createCopy(String info) {
        Period period = new Period(LocalDate.from(start), new ArrayList<>(), true, info);
        List<Month> copiedMonths = months.stream()
                .map(Month::createCopy)
                .collect(Collectors.toList());
        period.months.addAll(copiedMonths);
        return period;
    }

    public List<MonthlyBalance> getMonthlyBalances(PremiumSnapshot premiumSnapshot) {
        return months.stream()
                .map(month -> MonthlyBalance.builder()
                        .month(month.getYearMonth())
                        .componentPremiums(premiumSnapshot.getDetails(month.getYearMonth().atDay(1)))
                        .isPaid(month.isPaid())
                        .build())
                .collect(Collectors.toList());
    }

    public Month createMonth(YearMonth yearMonth, Amount premium) {
        return new Month(yearMonth, premium, false);
    }

    public Month createRenewalMonth(YearMonth yearMonth, Amount premium) {
        return new Month(yearMonth, premium, true);
    }

    public void addNewMonth(Month newMonth) {
        if (months.stream().anyMatch(month -> month.getYearMonth().equals(newMonth.getYearMonth()))) {
            throw new IllegalStateException();
        }
        months.add(newMonth);
    }

    public void markAsInvalid() {
        this.isValid = false;
    }

    private Optional<Month> getLastPaidMonth() {
        return months.stream()
                .filter(Month::isPaid)
                .max(Month::compareAscending);
    }

    public List<YearMonth> getRenewalMonths() {
        return months.stream()
                .filter(Month::isRenewal)
                .map(Month::getYearMonth)
                .collect(Collectors.toList());
    }

    private Optional<Month> getLastMonthWithPayment() {
        return months.stream()
                .filter(Month::hasPayment)
                .max(Month::compareAscending);
    }

    private Optional<Month> getLastMonth() {
        return months.stream()
                .max(Month::compareAscending);
    }

    public Optional<Month> getMonthOf(YearMonth month) {
        return months.stream()
                .filter(m -> m.getYearMonth().equals(month))
                .findAny();
    }

    private Optional<Month> getPreviousMonth(Month month) {
        return months.stream()
                .filter(m -> m.isBefore(month))
                .max(Month::compareAscending);
    }

    public YearMonth getLastPaidYearMonth() {
        return getLastPaidMonth()
                .map(Month::getYearMonth)
                .orElse(YearMonth.from(start).minusMonths(1));
    }

    public boolean isValid() {
        return isValid;
    }

}