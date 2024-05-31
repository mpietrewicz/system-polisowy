package pl.mpietrewicz.sp.modules.balance.domain.balance.month;

import lombok.Getter;
import lombok.NoArgsConstructor;
import pl.mpietrewicz.sp.ddd.canonicalmodel.publishedlanguage.snapshot.premium.PremiumSnapshot;
import pl.mpietrewicz.sp.ddd.sharedkernel.valueobject.Amount;
import pl.mpietrewicz.sp.ddd.sharedkernel.valueobject.PositiveAmount;
import pl.mpietrewicz.sp.ddd.support.infrastructure.repo.BaseEntity;
import pl.mpietrewicz.sp.modules.balance.domain.balance.month.state.Unpaid;
import pl.mpietrewicz.sp.modules.balance.exceptions.NoMonthsToRefundException;

import javax.persistence.AttributeOverride;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import java.time.LocalDate;
import java.time.YearMonth;

@Entity
@Getter
@NoArgsConstructor
public class Month extends BaseEntity implements LastMonth {

    private LocalDate yearMonth;

    @Enumerated(EnumType.STRING)
    private ChangeStatus changeStatus;

    @OneToOne(cascade = CascadeType.PERSIST)
    @JoinColumn(name = "month_state_id")
    private MonthState monthState;

    @Embedded
    @AttributeOverride(name = "value", column = @Column(name = "premium"))
    private PositiveAmount premium;

    private boolean renewal;

    public Month(YearMonth yearMonth, PositiveAmount premium, boolean renewal) {
        this.yearMonth = yearMonth.atDay(1);
        this.premium = premium;
        this.renewal = renewal;
        this.monthState = new Unpaid(this);
    }

    @Override
    public Amount pay(PositiveAmount payment) {
        return monthState.pay(payment);
    }

    @Override
    public Amount refund(PositiveAmount refund) throws NoMonthsToRefundException {
        return monthState.refund(refund);
    }

    @Override
    public Amount refund() {
        return monthState.refund();
    }

    @Override
    public boolean canPaidBy(PositiveAmount payment) {
        return monthState.canPaidBy(payment);
    }

    public boolean isPaid() {
        return monthState.isPaid();
    }

    @Override
    public boolean isUnpaid() {
        return !monthState.hasPayment();
    }

    @Override
    public LastMonth createNextMonth(PremiumSnapshot premiumSnapshot) {
        YearMonth nextYearMonth = YearMonth.from(this.yearMonth).plusMonths(1);
        return MonthFactory.create(nextYearMonth, premiumSnapshot, false);
    }

    public void changeState(MonthState monthState) {
        this.monthState = monthState;
    }

    public boolean isValid() {
        return changeStatus != ChangeStatus.REMOVED;
    }

    public Amount getPaid() {
        return monthState.getPaid();
    }

    public Month createCopy() {
        Month month = new Month(YearMonth.from(yearMonth), premium, renewal);
        month.changeState(this.monthState.createCopy(month));
        return month;
    }

    @Override
    public YearMonth getYearMonth() {
        return YearMonth.from(yearMonth);
    }

    public int compareAscending(Month month) {
        return YearMonth.from(this.yearMonth).compareTo(month.getYearMonth());
    }

    public int compareDescending(Month month) {
        return month.getYearMonth().compareTo(YearMonth.from(this.yearMonth));
    }

    public boolean isBefore(Month month) {
        return compareAscending(month) < 0;
    }

    public boolean isAfter(Month month) {
        return compareAscending(month) > 0;
    }

    public PositiveAmount getPremium() {
        return premium;
    }

    public boolean isTheSame(Month month) {
        return isTheSameYearMonth(month)
        && this.premium.equals(month.premium)
        && this.renewal == month.renewal
        && this.monthState.isTheSame(month.monthState);
    }

    public boolean isTheSameYearMonth(Month month) {
        return this.yearMonth.equals(month.yearMonth);
    }

    public void setAs(ChangeStatus changeStatus) {
        this.changeStatus = changeStatus;
    }

}