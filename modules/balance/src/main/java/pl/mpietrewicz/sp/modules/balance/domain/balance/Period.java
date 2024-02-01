package pl.mpietrewicz.sp.modules.balance.domain.balance;

import lombok.NoArgsConstructor;
import pl.mpietrewicz.sp.ddd.annotations.domain.DomainEntity;
import pl.mpietrewicz.sp.ddd.canonicalmodel.publishedlanguage.MonthlyBalance;
import pl.mpietrewicz.sp.ddd.canonicalmodel.publishedlanguage.snapshot.premium.PremiumSnapshot;
import pl.mpietrewicz.sp.ddd.sharedkernel.Amount;
import pl.mpietrewicz.sp.ddd.sharedkernel.PositiveAmount;
import pl.mpietrewicz.sp.modules.balance.domain.balance.month.Month;
import pl.mpietrewicz.sp.modules.balance.domain.balance.operation.PaymentData;
import pl.mpietrewicz.sp.modules.balance.domain.balance.paymentpolicy.PaymentPolicy;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static pl.mpietrewicz.sp.ddd.sharedkernel.Amount.ZERO;

@DomainEntity
@Embeddable
@NoArgsConstructor
public class Period {

    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "operation_id")
    private List<Month> months;

    private LocalDate start;

    public Period(LocalDate start, List<Month> months) {
        this.start = start;
        this.months = months;
    }

    public void tryPay(PaymentPolicy paymentPolicy, PaymentData paymentData, PremiumSnapshot premiumSnapshot) {
        MonthToPay monthToPay = paymentPolicy.getMonthToPay(this, paymentData);
        Month month = monthToPay.getMonth();
        Amount amount = monthToPay.getAmount();

        do {
            amount = month.pay(amount.castToPositive());
            if (month.isPaid() && amount.isPositive()) {
                month = createNextMonth(month, premiumSnapshot);
            }
        } while (amount.isPositive());
    }

    public void tryRefund(Amount refund) {
        if (refund.isPositive()) {
            refund = refund.castToPositive();
        } else {
            return;
        }
        Month month = getLastMonthWithPayment() // todo: tutaj zwracam od paid lub unpaid
                .orElseThrow(() -> new IllegalStateException("Nie można zwrócić, bo nie ma opłaconych miesięcy"));

        do {
            refund = month.refund(refund.castToPositive());
            if (month.isUnpaid()) { // todo: dla umowy bez okresu prolongaty to będzie tylko pierwszy warunek
                months.remove(month);
                month = getPreviousMonth(month)
                        .orElseThrow(() -> new IllegalStateException("Nie można zwrócić, bo nie ma opłaconych miesięcy"));
            }
        } while (refund.isPositive());

    }

    public Amount tryRefundUpTo(YearMonth yearMonth) {
        Amount amountToRefund = months.stream()
                .filter(month -> month.getYearMonth().compareTo(yearMonth) >= 0)
                .map(Month::getPaid)
                .reduce(ZERO, Amount::add);

        tryRefund(amountToRefund);
        return amountToRefund;
    }

    private Month createNextMonth(Month month, PremiumSnapshot premiumSnapshot) {
        YearMonth yearMonth = month.getYearMonth().plusMonths(1);
        PositiveAmount premium = premiumSnapshot.getAmountAt(yearMonth.atDay(1));
        Month nextMonth = createMonth(yearMonth, premium);
        addNewMonth(nextMonth);
        return nextMonth;
    }

    public Period createCopy() {
        List<Month> copiedMonths = months.stream()
                .map(Month::createCopy)
                .collect(Collectors.toList());
        return new Period(LocalDate.from(start), copiedMonths);
    }

    public List<MonthlyBalance> getMonthlyBalances(PremiumSnapshot premiumSnapshot) {
        return months.stream()
                .map(month -> MonthlyBalance.builder()
                        .month(month.getYearMonth())
                        .componentPremiums(premiumSnapshot.getDetails())
                        .isPaid(month.isPaid())
                        .build())
                .collect(Collectors.toList());
    }

    public static Month createMonth(YearMonth yearMonth, Amount premium) {
        return Month.init(yearMonth, premium);
    }

    public void addNewMonth(Month newMonth) {
        if (months.stream().anyMatch(month -> month.getYearMonth().equals(newMonth.getYearMonth()))) {
            throw new IllegalStateException();
        }
        months.add(newMonth);
    }

    protected Optional<Month> getLastPaidMonth() {
        return months.stream()
                .filter(Month::isPaid)
                .max(Month::compareAscending);
    }

    protected Optional<Month> getLastMonthWithPayment() {
        return months.stream()
                .filter(Month::hasPayment)
                .max(Month::compareAscending);
    }

    protected Optional<Month> getLastMonth() {
        return months.stream()
                .max(Month::compareAscending);
    }

    public Optional<Month> getNextMonth(Month month) {
        return months.stream()
                .filter(m -> m.isAfter(month))
                .min(Month::compareAscending);
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

    public boolean isLastPaidMonthBefore(LocalDate date) {
        return months.stream()
                .filter(Month::isPaid)
                .max(Month::compareAscending)
                .map(lastPaid -> lastPaid.getYearMonth().isBefore(YearMonth.from(date)))
                .orElse(false);
    }

    public YearMonth getLastPaidYearMonth() {
        return getLastPaidMonth()
                .map(Month::getYearMonth)
                .orElse(YearMonth.from(start).minusMonths(1));
    }

    public Amount getLastUnderpayment() {
        return getLastMonth()
                .map(Month::getPaid)
                .orElse(ZERO);
    }

}