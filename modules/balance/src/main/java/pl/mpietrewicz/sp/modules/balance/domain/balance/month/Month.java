package pl.mpietrewicz.sp.modules.balance.domain.balance.month;

import lombok.Getter;
import lombok.NoArgsConstructor;
import pl.mpietrewicz.sp.ddd.canonicalmodel.publishedlanguage.snapshot.premium.PremiumSnapshot;
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
import javax.persistence.OneToOne;
import java.time.LocalDate;
import java.time.YearMonth;

@Entity
@Getter
@NoArgsConstructor
public class Month extends BaseEntity implements LastMonth {

    private LocalDate yearMonth;

    @OneToOne(cascade = CascadeType.PERSIST)
    @JoinColumn(name = "month_state_id")
    private MonthState monthState;

    @Embedded
    @AttributeOverride(name = "value", column = @Column(name = "premium"))
    private Amount premium;

    private boolean renewal;

    public Month(YearMonth yearMonth, Amount premium, boolean renewal) {
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
    public Amount refund(PositiveAmount refund) {
        return monthState.refund(refund);
    }

    @Override
    public Amount refund() {
        return monthState.refund();
    }

    @Override
    public boolean canPaidBy(Amount payment) {
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

    public Amount getPaid() {
        return monthState.getPaid();
    }

    public Month createCopy() {
        Month month = new Month(YearMonth.from(yearMonth), premium, renewal);
        month.changeState(this.monthState.createCopy(month));
        return month;
    }

    public YearMonth getYearMonth() {
        return YearMonth.from(yearMonth);
    }

    public int compareAscending(Month month) {
        return YearMonth.from(this.yearMonth).compareTo(month.getYearMonth());
    }

    public Amount getPremium() {
        return premium;
    }

}