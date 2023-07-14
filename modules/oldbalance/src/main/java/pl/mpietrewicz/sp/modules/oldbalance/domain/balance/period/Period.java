package pl.mpietrewicz.sp.modules.oldbalance.domain.balance.period;

import lombok.Getter;
import pl.mpietrewicz.sp.modules.oldbalance.domain.balance.Balance;
import pl.mpietrewicz.sp.modules.oldbalance.domain.balance.PremiumDue;
import pl.mpietrewicz.sp.modules.oldbalance.domain.balance.DueChangeStrategy;
import pl.mpietrewicz.sp.modules.oldbalance.domain.balance.period.state.Unpaid;
import pl.mpietrewicz.sp.ddd.annotations.domain.ValueObject;
import pl.mpietrewicz.sp.ddd.support.domain.BaseEntity;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import java.math.BigDecimal;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@ValueObject
@Entity
public class Period extends BaseEntity {

    public static final Comparator<Period> ASCENDING = Comparator.comparing(Period::getMonth);
    public static final Comparator<Period> DESCENDING = Comparator.comparing(Period::getMonth).reversed();

    private YearMonth month;

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER, orphanRemoval = true)
    @JoinColumn(name = "premium_due_id")
    private PremiumDue premiumDue;

    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinColumn(name = "balance_id")
    private Balance balance;

    @OneToOne(cascade = CascadeType.ALL)
    @Getter
    private Period previous;

    @OneToOne(cascade = CascadeType.ALL)
    @Getter
    private Period next;

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER, orphanRemoval = true)
    @JoinColumn(name = "period_state_id")
    private PeriodState periodState;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "period_id")
    private List<PeriodStateSnapshot> periodStateSnapshots = new ArrayList<>();

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "period_id")
    private List<NewPeriodSnapshot> newPeriodSnapshots = new ArrayList<>();

    /*
     todo: wysyłać zdarzenie na zewnątrz (dla przygotowania przypisu), po zmianie:
     - stanu opłacenia
     - due
     - niedopłaty
     - nadpłaty
     - okresu prolongaty?
     */

    public Period() {
    }

    public Period(YearMonth month, PremiumDue premiumDue, PeriodStatus periodStatus,
                  BigDecimal underpayment, BigDecimal overpayment) {
        this.month = month;
        this.premiumDue = premiumDue;
        this.periodState = PeriodStateFactory.createState(this, periodStatus, underpayment, overpayment);
    }

    public Period(YearMonth month, PremiumDue premiumDue, PeriodStatus periodStatus,
                  BigDecimal underpayment, BigDecimal overpayment,
                  Period previous) {
        this.month = month;
        this.premiumDue = premiumDue;
        this.periodState = PeriodStateFactory.createState(this, periodStatus, underpayment, overpayment);
        this.previous = previous;
    }


    public void tryPay(BigDecimal payment) {
        periodState.pay(payment);
    }

    public void tryRefund(BigDecimal refund) {
        periodState.refund(refund);
    }

    public Period tryCreateNextPeriod() {
        if (getNext().isPresent()) {
            throw new IllegalStateException("The next period is present!");
        }
        return periodState.createNextPeriod();
    }

    public void updatePremiumDue(final DueChangeStrategy dueChangeStrategy) {
        dueChangeStrategy.execute(premiumDue);
        getNext().ifPresent(
                nextPeriod -> nextPeriod.updatePremiumDue(dueChangeStrategy)
        );
    }

    public BigDecimal getUnderpayment() {
        return periodState.getUnderpayment();
    }

    public BigDecimal getOverpayment() {
        return periodState.getOverpayment();
    }

    public YearMonth getMonth() {
        return YearMonth.from(month);
    }

    public boolean isBefore(YearMonth month) {
        return this.month.compareTo(month) < 0;
    }

    public boolean isAfter(YearMonth month) {
        return this.month.compareTo(month) > 0;
    }

    public boolean isAt(YearMonth month) {
        return this.month.compareTo(month) == 0;
    }

    public PeriodStatus getStatus() {
        return periodState.getStatus();
    }

    public boolean isPaid() {
        return periodState.getStatus().isPaid();
    }

    public boolean isCovered() {
        return periodState.getStatus().isCovered();
    }

    public boolean isNotPaid() {
        return periodState.getStatus().isNotPaid();
    }

    public BigDecimal getPremiumDue() {
        return premiumDue.getPremiumDue();
    }

    public Optional<Period> getPrevious() {
        return Optional.ofNullable(previous);
    }

    public Optional<Period> getNext() {
        return Optional.ofNullable(next);
    }

    public void eraseState() {
        this.periodState = new Unpaid(this);
    }

    protected void saveSnapshot() {
        PeriodStateSnapshot snapshot = new PeriodStateSnapshot(
                month.atDay(1),
                periodState.getStatus(),
                premiumDue.getPremiumDue(),
                periodState.getUnderpayment(),
                periodState.getOverpayment()
        );
        periodStateSnapshots.add(snapshot);
    }

    public void changeState(PeriodState periodState) {
        saveSnapshot();
        this.periodState = periodState;
    }

    public void setBalance(Balance balance) { // todo: co z tym? to tylko jest do testów // todo: do usunięcia
        this.balance = balance;
    }

    public void setNext(Period next) {
        if (this.next == null) {
            this.next = next;
        } else {
            throw new IllegalStateException();
        }
    }

    public void setPervious(Period previous) {
        if (this.previous == null) {
            this.previous = previous;
        } else {
            throw new IllegalStateException();
        }
    }

    public PremiumDue getDue() {
        return premiumDue;
    }
}