package pl.mpietrewicz.sp.modules.balance.domain.balance.month;

import lombok.Getter;
import pl.mpietrewicz.sp.ddd.sharedkernel.Amount;
import pl.mpietrewicz.sp.ddd.sharedkernel.PositiveAmount;
import pl.mpietrewicz.sp.modules.balance.domain.balance.month.state.Unpaid;
import pl.mpietrewicz.sp.modules.balance.exceptions.PaymentException;
import pl.mpietrewicz.sp.modules.balance.exceptions.RefundException;

import java.time.YearMonth;

@Getter
public class Month {

    private Long id;

    private final YearMonth yearMonth;

    private MonthState monthState;

    private final Amount premium; // todo: tutaj można dać obiekt Premium gdzie będzie lista komponentów i ich składek

    private final boolean renewal;

    public Month(Long id, YearMonth yearMonth, Amount premium, boolean renewal) {
        this.id = id;
        this.yearMonth = yearMonth;
        this.premium = premium;
        this.renewal = renewal;
    }

    public Month(YearMonth yearMonth, Amount premium, boolean renewal) {
        this.yearMonth = yearMonth;
        this.premium = premium;
        this.renewal = renewal;
        this.monthState = new Unpaid(this);
    }

    public void changeState(MonthState monthState) {
        this.monthState = monthState;
    }

    public Amount pay(PositiveAmount payment) throws PaymentException {
        return monthState.pay(payment);
    }

    public Amount refund(PositiveAmount refund) throws RefundException {
        return monthState.refund(refund);
    }

    public Amount refund() {
        Amount refunded = getPaid();
        changeState(new Unpaid(this));
        return refunded;
    }

    public boolean canPaidBy(Amount payment) {
        return monthState.canPaidBy(payment);
    }

    public PaidStatus getPaidStatus() {
        return monthState.getPaidStatus();
    }

    public boolean isPaid() {
        return monthState.isPaid();
    }

    public boolean hasPayment() {
        return monthState.hasPayment();
    }

    public Amount getPaid() {
        return monthState.getPaid();
    }

    public Month createCopy() {
        Month month = new Month(yearMonth, premium, renewal);
        month.changeState(this.monthState.createCopy(month));
        return month;
    }

    public YearMonth getYearMonth() {
        return yearMonth;
    }

    public int compareAscending(Month month) {
        return this.yearMonth.compareTo(month.getYearMonth());
    }

    public int compareDescending(Month month) {
        return month.getYearMonth().compareTo(this.yearMonth);
    }

    public boolean isBefore(Month month) {
        return compareAscending(month) < 0;
    }

    public Amount getPremium() {
        return premium;
    }

    @Override
    public String toString() {
        return yearMonth + ", " + getPaidStatus();
    }

}