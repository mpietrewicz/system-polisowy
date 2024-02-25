package pl.mpietrewicz.sp.modules.balance.domain.balance.month;

import lombok.NoArgsConstructor;
import pl.mpietrewicz.sp.ddd.annotations.domain.DomainEntity;
import pl.mpietrewicz.sp.ddd.sharedkernel.Amount;
import pl.mpietrewicz.sp.ddd.sharedkernel.PositiveAmount;
import pl.mpietrewicz.sp.ddd.support.infrastructure.repo.BaseEntity;
import pl.mpietrewicz.sp.modules.balance.domain.balance.month.state.Unpaid;

import javax.persistence.AttributeOverride;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import java.time.YearMonth;

@DomainEntity
@Entity
@NoArgsConstructor
public class Month extends BaseEntity {

    public YearMonth yearMonth;

    @Embedded
    @AttributeOverride(name = "value", column = @Column(name = "premium"))
    public Amount premium;

    @Embedded
    @AttributeOverride(name = "value", column = @Column(name = "paid"))
    public Amount paid;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "month_state_id")
    private MonthState monthState;

    public Month(YearMonth yearMonth, Amount premium, Amount paid, MonthState monthState) {
        this.yearMonth = yearMonth;
        this.premium = premium;
        this.paid = paid;
        this.monthState = monthState;
    }

    public Month(YearMonth yearMonth, Amount premium) {
        this.yearMonth = yearMonth;
        this.premium = premium;
        this.paid = Amount.ZERO;
        this.monthState = new Unpaid(this);
    }

    public void changeState(MonthState monthState) {
        this.monthState = monthState;
    }

    public Amount pay(PositiveAmount payment) {
        return monthState.pay(payment);
    }

    public Amount refund(PositiveAmount refund) {
        return monthState.refund(refund);
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

    public Month createCopy() {
        Month month = new Month(yearMonth, premium);
        month.changeState(monthState.createCopy(month, paid));
        return month;
    }

    public Amount refund() {
        Amount refunded = paid;
        changeState(new Unpaid(this));
        return refunded;
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

    public boolean isAfter(Month month) {
        return compareAscending(month) > 0;
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