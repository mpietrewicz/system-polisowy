package pl.mpietrewicz.sp.modules.balance.domain.balance.month;

import lombok.NoArgsConstructor;
import pl.mpietrewicz.sp.ddd.annotations.domain.ValueObject;
import pl.mpietrewicz.sp.ddd.sharedkernel.Amount;
import pl.mpietrewicz.sp.ddd.sharedkernel.PositiveAmount;
import pl.mpietrewicz.sp.ddd.support.infrastructure.repo.BaseEntity;
import pl.mpietrewicz.sp.modules.balance.domain.balance.month.state.Unpaid;

import javax.persistence.AttributeOverride;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.OneToOne;

@ValueObject
@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
//@DiscriminatorColumn(name = "status")
@NoArgsConstructor
public abstract class MonthState extends BaseEntity {

    @Enumerated(EnumType.STRING)
    private PaidStatus status;

    @OneToOne(mappedBy = "monthState")
    protected Month month;

    @Embedded
    @AttributeOverride(name = "aggregateId", column = @Column(name = "paid", nullable = false))
    private Amount paid;

    protected MonthState(Month month, Amount paid, PaidStatus status) {
        this.month = month;
        this.paid = paid;
        this.status = status;
    }

    public abstract Amount pay(PositiveAmount payment);

    public abstract Amount refund(PositiveAmount refund);

    public Amount refund() {
        Amount refunded = getPaid();
        if (refunded.isPositive()) {
            month.changeState(new Unpaid(month));
            return refunded;
        } else {
            return Amount.ZERO;
        }
    }

    public abstract boolean canPaidBy(Amount payment);

    public abstract boolean isPaid();

    public abstract boolean hasPayment();

    public abstract MonthState createCopy(Month month);

    public PaidStatus getPaidStatus() {
        return status;
    }

    public Amount getPaid() {
        return paid;
    }

}