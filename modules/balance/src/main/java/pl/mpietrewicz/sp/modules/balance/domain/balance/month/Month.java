package pl.mpietrewicz.sp.modules.balance.domain.balance.month;

import lombok.NoArgsConstructor;
import pl.mpietrewicz.sp.ddd.annotations.domain.DomainEntity;
import pl.mpietrewicz.sp.ddd.sharedkernel.Amount;
import pl.mpietrewicz.sp.ddd.sharedkernel.PositiveAmount;
import pl.mpietrewicz.sp.modules.balance.ddd.support.domain.BaseEntity;

import javax.persistence.AttributeOverride;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import java.time.YearMonth;

import static pl.mpietrewicz.sp.ddd.sharedkernel.Amount.ZERO;

@DomainEntity
@Entity
@NoArgsConstructor
public class Month extends BaseEntity {

    private YearMonth yearMonth;

    @Embedded
    @AttributeOverride(name = "value", column = @Column(name = "premium"))
    protected Amount premium;

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER, orphanRemoval = true)
    @JoinColumn(name = "month_state_id")
    private MonthState monthState;

    public Month(YearMonth yearMonth, MonthStatus monthStatus, Amount premium, Amount paid) {
        this.yearMonth = yearMonth;
        this.premium = premium;
        this.monthState = MonthStateFactory.createState(this, monthStatus, paid);
    }

    public Month(YearMonth yearMonth, Amount premium, MonthState monthState) {
        this.yearMonth = yearMonth;
        this.premium = premium;
        this.monthState = monthState;
    }

    public static Month init(YearMonth yearMonth, Amount premium) {
        return new Month(yearMonth, MonthStatus.UNPAID, premium, ZERO);
    }

    public Amount pay(PositiveAmount payment) {
        return monthState.pay(payment);
    }

    public Amount refund(PositiveAmount refund) {
        return monthState.refund(refund);
    }

    public YearMonth getYearMonth() {
        return yearMonth;
    }

    public Month createCopy() { // todo: tak średnio to wygląda
        Month monthCopy = new Month(yearMonth, premium, monthState);
        MonthState monthStateCopy = MonthStateFactory.createState(monthCopy, monthState.getStatus(),
                monthState.paid);
        monthCopy.changeState(monthStateCopy);
        return monthCopy;
    }

    public boolean canPaidBy(Amount payment) {
        return monthState.canPaidBy(payment);
    }

    public void changeState(MonthState monthState) {
        this.monthState = monthState;
    }

    public boolean isPaid() {
        return monthState.isPaid();
    }

    public boolean hasPayment() {
        return monthState.hasPayment();
    }

    public boolean isUnpaid() {
        return monthState.isUnpaid();
    }

    public int compareAscending(Month month) {
        return this.yearMonth.compareTo(month.getYearMonth());
    }

    public int compareDescending(Month month) {
        return month.getYearMonth().compareTo(this.yearMonth);
    }

    public Amount getPaid() {
        return monthState.getPaid();
    }

    @Override
    public String toString() {
        return yearMonth +
                ", " + monthState.getStatus();
    }

    public boolean isAfter(Month month) {
        return compareAscending(month) > 0;
    }

    public boolean isBefore(Month month) {
        return compareAscending(month) < 0;
    }

    public boolean equals(Month month) {
        return compareAscending(month) == 0;
    }

    public Amount getPremium() {
        return premium;
    }
}