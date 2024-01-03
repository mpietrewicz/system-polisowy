package pl.mpietrewicz.sp.modules.balance.domain.balance.month;

import lombok.Getter;
import lombok.NoArgsConstructor;
import pl.mpietrewicz.sp.ddd.annotations.domain.DomainEntity;
import pl.mpietrewicz.sp.ddd.sharedkernel.Amount;
import pl.mpietrewicz.sp.ddd.sharedkernel.PositiveAmount;
import pl.mpietrewicz.sp.modules.balance.ddd.support.domain.BaseEntity;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Inheritance;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import java.time.YearMonth;
import java.util.List;
import java.util.Optional;

import static javax.persistence.InheritanceType.SINGLE_TABLE;
import static pl.mpietrewicz.sp.ddd.sharedkernel.Amount.ZERO;
import static pl.mpietrewicz.sp.modules.balance.domain.balance.month.MonthStatus.OVERPAID;
import static pl.mpietrewicz.sp.modules.balance.domain.balance.month.MonthStatus.PAID;
import static pl.mpietrewicz.sp.modules.balance.domain.balance.month.MonthStatus.UNDERPAID;
import static pl.mpietrewicz.sp.modules.balance.domain.balance.month.MonthStatus.UNPAID;

@DomainEntity
@Entity
@Inheritance(strategy = SINGLE_TABLE)
@NoArgsConstructor
public abstract class MonthState extends BaseEntity {

    @OneToOne
    @JoinColumn(name = "month_id")
    protected Month month;

    @Getter
    @Enumerated(EnumType.STRING)
    private MonthStatus status;

    @Embedded
    @AttributeOverrides({@AttributeOverride(name = "value", column = @Column(name = "underpayment"))})
    protected Amount underpayment = Amount.ZERO; // todo: wydaje mi sie, że te wartosci mogę spokojnie przenieść do konkretnych implementacji, a entity sobie poradzi

    @Embedded
    @AttributeOverrides({@AttributeOverride(name = "value", column = @Column(name = "overpayment"))})
    protected Amount overpayment = Amount.ZERO;

    public MonthState(Month month, MonthStatus status, Amount underpayment, Amount overpayment) {
        this.month = month;
        this.status = status;
        this.underpayment = underpayment;
        this.overpayment = overpayment;
    }

    public abstract Amount pay(PositiveAmount payment, Optional<Month> nextMonth); // todo: tak naprawdę potrzebuję tylko wiedzieć czy istniej następny okres!

    public abstract Amount refund(PositiveAmount refund, Optional<Month> previousMonth);

    public abstract boolean canPaidBy(Amount payment);

    public abstract Month createNextMonth(List<ComponentPremium> componentPremiums);

    public abstract Amount getPaid();

    public abstract MonthState getCopy();

    public boolean isPaid() {
        return status.isPaid();
    }

    public boolean isNotPaid() {
        return status.isNotPaid();
    }

    public MonthStatus getStatus() {
        return status;
    }

    protected Month createUnpaid(YearMonth yearMonth, List<ComponentPremium> componentPremiums) {
        return new Month(yearMonth, UNPAID, ZERO, ZERO, componentPremiums);
    }

    protected Month createUnderpaid(YearMonth yearMonth, Amount underpayment, List<ComponentPremium> componentPremiums) {
        return new Month(yearMonth, UNDERPAID, underpayment, ZERO, componentPremiums);
    }

    protected Month createPaid(YearMonth yearMonth, List<ComponentPremium> componentPremiums) {
        return new Month(yearMonth, PAID, ZERO, ZERO, componentPremiums);
    }

    protected Month createOverpaid(YearMonth yearMonth, Amount overpayment, List<ComponentPremium> componentPremiums) {
        return new Month(yearMonth, OVERPAID, ZERO, overpayment, componentPremiums);
    }

    protected Amount getPremium() {
        return month.getPremium();
    }

    protected boolean paidIsLessThan(Amount amount) {
        return getPaid().isLessThan(amount);
    }

    protected boolean paidEquals(Amount amount) {
        return getPaid().equals(amount);
    }

    protected boolean paidIsHigherThan(Amount amount) {
        return getPaid().isHigherThan(amount);
    }


}