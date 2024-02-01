package pl.mpietrewicz.sp.modules.balance.domain.balance.month;

import lombok.Getter;
import lombok.NoArgsConstructor;
import pl.mpietrewicz.sp.ddd.annotations.domain.DomainEntity;
import pl.mpietrewicz.sp.ddd.sharedkernel.Amount;
import pl.mpietrewicz.sp.ddd.sharedkernel.PositiveAmount;
import pl.mpietrewicz.sp.modules.balance.ddd.support.domain.BaseEntity;

import javax.persistence.AttributeOverride;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Inheritance;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;

import static javax.persistence.InheritanceType.SINGLE_TABLE;

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
    @AttributeOverride(name = "value", column = @Column(name = "paid"))
    protected Amount paid;

    public MonthState(Month month, MonthStatus status, Amount paid) {
        this.month = month;
        this.status = status;
        this.paid = paid;
    }

    public MonthState(Month month, MonthStatus status) {
        this.month = month;
        this.status = status;
        this.paid = getPremium();
    }

    public abstract Amount pay(PositiveAmount payment);

    public abstract Amount refund(PositiveAmount refund);

    public abstract boolean canPaidBy(Amount payment);

    public Amount getPaid() {
        return paid;
    }

    public Amount getPremium() {
        return month.getPremium();
    }

    public boolean isPaid() {
        return status.isPaid();
    }

    public boolean hasPayment() {
        return status.hasPayment();
    }

    public boolean isUnpaid() {
        return status.isUnpaid();
    }

    public MonthStatus getStatus() {
        return status;
    }

}