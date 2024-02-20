package pl.mpietrewicz.sp.modules.balance.domain.balance.month;

import pl.mpietrewicz.sp.ddd.annotations.domain.DomainEntity;
import pl.mpietrewicz.sp.ddd.sharedkernel.Amount;
import pl.mpietrewicz.sp.ddd.sharedkernel.PositiveAmount;
import pl.mpietrewicz.sp.ddd.support.infrastructure.repo.BaseEntity;

import javax.persistence.AttributeOverride;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.PostLoad;
import javax.persistence.Transient;
import java.time.YearMonth;

@DomainEntity
@Entity
public class Month extends BaseEntity {

    private YearMonth yearMonth;

    @Transient
    private MonthState currentState;

    @Embedded
    @AttributeOverride(name = "value", column = @Column(name = "premium"))
    protected Amount premium;

    @Enumerated(EnumType.STRING)
    public PaidStatus paidStatus;

    @Embedded
    @AttributeOverride(name = "value", column = @Column(name = "paid"))
    public Amount paid;

    public Month() {
    }

    public Month(YearMonth yearMonth, Amount premium, PaidStatus paidStatus, Amount paid) {
        this.yearMonth = yearMonth;
        this.premium = premium;
        this.currentState = MonthStateFactory.createState(paidStatus, paid);
        this.paidStatus = currentState.getPaidStatus();
        this.paid = currentState.getPaid();
    }

    @PostLoad
    private void postLoad() {
        this.currentState = MonthStateFactory.createState(paidStatus, paid);
    }

    public static Month init(YearMonth yearMonth, Amount premium) {
        return new Month(yearMonth, premium, PaidStatus.UNPAID, Amount.ZERO);
    }

    public Amount pay(PositiveAmount payment) {
        return currentState.pay(this, payment);
    }

    public Amount refund(PositiveAmount refund) {
        return currentState.refund(this, refund);
    }

    public Amount refund() {
        return currentState.refund(this);
    }

    public YearMonth getYearMonth() {
        return yearMonth;
    }

    public Month createCopy() {
        return new Month(yearMonth, premium, paidStatus, paid);
    }

    public boolean canPaidBy(Amount payment) {
        return currentState.canPaidBy(this, payment);
    }

    public void changeState(MonthState monthState) {
        this.currentState = monthState;
        this.paidStatus = monthState.getPaidStatus();
        this.paid = monthState.getPaid();
    }

    public boolean isPaid() {
        return currentState.isPaid();
    }

    public boolean hasPayment() {
        return currentState.hasPayment();
    }

    public int compareAscending(Month month) {
        return this.yearMonth.compareTo(month.getYearMonth());
    }

    public int compareDescending(Month month) {
        return month.getYearMonth().compareTo(this.yearMonth);
    }

    public Amount getPaid() {
        return currentState.getPaid();
    }

    @Override
    public String toString() {
        return yearMonth + ", " + currentState.getPaidStatus();
    }

    public boolean isAfter(Month month) {
        return compareAscending(month) > 0;
    }

    public boolean isBefore(Month month) {
        return compareAscending(month) < 0;
    }

    public Amount getPremium() {
        return premium;
    }
}