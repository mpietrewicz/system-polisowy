package pl.mpietrewicz.sp.modules.balance.domain.balance.month;

import lombok.NoArgsConstructor;
import pl.mpietrewicz.sp.ddd.annotations.domain.ValueObject;
import pl.mpietrewicz.sp.ddd.sharedkernel.valueobject.Amount;
import pl.mpietrewicz.sp.ddd.sharedkernel.valueobject.PositiveAmount;
import pl.mpietrewicz.sp.ddd.support.infrastructure.repo.BaseEntity;
import pl.mpietrewicz.sp.modules.balance.exceptions.NoMonthsToRefundException;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.OneToOne;

@ValueObject
@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@NoArgsConstructor
public abstract class MonthState extends BaseEntity {

    @Enumerated(EnumType.STRING)
    private PaidStatus status;

    @OneToOne(mappedBy = "monthState")
    protected Month month;

    protected MonthState(Month month, PaidStatus status) {
        this.month = month;
        this.status = status;
    }

    public abstract Amount pay(PositiveAmount payment);

    public abstract Amount refund(PositiveAmount refund) throws NoMonthsToRefundException;

    public abstract Amount refund();

    public abstract boolean canPaidBy(PositiveAmount payment);

    public abstract boolean isPaid();

    public abstract boolean hasPayment();

    public abstract MonthState createCopy(Month month);

    public abstract Amount getPaid();

    public boolean isTheSame(MonthState monthState) {
        return getPaid().equals(monthState.getPaid())
        && this.status == monthState.status;
    }

}