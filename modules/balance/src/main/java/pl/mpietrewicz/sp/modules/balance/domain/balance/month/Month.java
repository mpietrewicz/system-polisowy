package pl.mpietrewicz.sp.modules.balance.domain.balance.month;

import lombok.NoArgsConstructor;
import pl.mpietrewicz.sp.ddd.annotations.domain.ValueObject;
import pl.mpietrewicz.sp.ddd.support.domain.BaseEntity;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import java.math.BigDecimal;
import java.time.YearMonth;
import java.util.Optional;

@ValueObject
@Entity
@NoArgsConstructor
public class Month extends BaseEntity { // todo: zmienić nazwę -> to nie może sie nazywać month!

    private YearMonth month;

    private BigDecimal premium;

    @OneToOne(cascade = CascadeType.ALL)
    private Month previous;

    @OneToOne(cascade = CascadeType.ALL)
    private Month next;

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER, orphanRemoval = true)
    @JoinColumn(name = "month_state_id")
    private MonthState monthState;

    public Month(YearMonth month, BigDecimal premium, MonthStatus monthStatus,
                 BigDecimal underpayment, BigDecimal overpayment) {
        this.month = month;
        this.premium = premium;
        this.monthState = MonthStateFactory.createState(this, monthStatus, underpayment, overpayment);
    }

    public Month(YearMonth month, BigDecimal premium, MonthStatus monthStatus,
                 BigDecimal underpayment, BigDecimal overpayment, Month previous) {
        this.month = month;
        this.premium = premium;
        this.monthState = MonthStateFactory.createState(this, monthStatus, underpayment, overpayment);
        this.previous = previous;
    }

    public Month createNextMonth() {
        return monthState.createNextMonth(getPremium());
    }

    public Month createNextMonth(BigDecimal premium) {
        return monthState.createNextMonth(premium);
    }

    public void tryPay(BigDecimal payment) {
        if (payment.signum() > 0) {
            monthState.pay(payment);
        }
    }

    public void tryRefund(BigDecimal refund) {
        if (refund.signum() > 0) {
            monthState.refund(refund);
        }
    }

    public void invalidate() {
        if (!isTheLastOne()) {
            throw new IllegalStateException("Nie można usuwać oresów innych niż ostatni");
        }
        BigDecimal paid = monthState.getPaid();
        getPrevious().ifPresent(month -> {
            month.setNext(null);
            month.tryPay(paid);
        });
    }

    public YearMonth getYearMonth() {
        return month;
    }

    public BigDecimal getPremium() {
        return premium;
    }

    public Month createCopy() {
        return new Month(month, premium, monthState.getStatus(),
                monthState.getUnderpayment(), monthState.getOverpayment());
    }

    public Optional<Month> getPrevious() {
        return Optional.ofNullable(previous);
    }

    public Optional<Month> getNext() {
        return Optional.ofNullable(next);
    }

    public void changeState(MonthState monthState) {
        this.monthState = monthState;
    }

    public void setNext(Month next) {
        this.next = next;
    }

    public void setPervious(Month previous) {
        this.previous = previous;
    }

    public boolean isPaid() {
        return monthState.isPaid();
    }

    public boolean isNotPaid() {
        return monthState.isNotPaid();
    }

    private boolean isTheLastOne() {
        return getNext().isEmpty();
    }

    public int orderComparator(Month month) {
        return this.month.compareTo(month.getYearMonth());
    }

    @Override
    public String toString() {
        return month +
                ", " + premium +
                ", " + monthState.getStatus();
    }
}