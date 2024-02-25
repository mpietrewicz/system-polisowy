package pl.mpietrewicz.sp.modules.balance.domain.balance.month;

import lombok.NoArgsConstructor;
import pl.mpietrewicz.sp.ddd.sharedkernel.Amount;
import pl.mpietrewicz.sp.ddd.sharedkernel.PositiveAmount;
import pl.mpietrewicz.sp.modules.balance.domain.balance.month.state.Overpaid;
import pl.mpietrewicz.sp.modules.balance.domain.balance.month.state.Paid;
import pl.mpietrewicz.sp.modules.balance.domain.balance.month.state.Underpaid;
import pl.mpietrewicz.sp.modules.balance.domain.balance.month.state.Unpaid;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.OneToOne;

@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@NoArgsConstructor
public abstract class MonthState {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private PaidStatus status;

    @OneToOne(mappedBy = "monthState", cascade = CascadeType.ALL, orphanRemoval = true)
    protected Month month;

    public MonthState(Month month, Amount paid, PaidStatus status) {
        this.month = month;
        this.month.paid = paid;
        this.status = status;
    }

    public abstract Amount pay(PositiveAmount payment);

    public abstract Amount refund(PositiveAmount refund);

    public abstract boolean canPaidBy(Amount payment);

    public abstract PaidStatus getPaidStatus();

    public abstract boolean isPaid();

    public abstract boolean hasPayment();

    public MonthState createCopy(Month month, Amount paid) {
        switch (status) {
            case OVERPAID:
                return new Overpaid(month, paid);
            case PAID:
                return new Paid(month, paid);
            case UNDERPAID:
                return new Underpaid(month, paid);
            case UNPAID:
                return new Unpaid(month);
            default:
                throw new IllegalStateException();
        }
    }

}