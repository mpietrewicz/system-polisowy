package pl.mpietrewicz.sp.modules.balance.domain.balance.month;

import pl.mpietrewicz.sp.ddd.annotations.domain.ValueObject;
import pl.mpietrewicz.sp.ddd.sharedkernel.Amount;
import pl.mpietrewicz.sp.ddd.sharedkernel.PositiveAmount;
import pl.mpietrewicz.sp.modules.balance.domain.balance.month.state.Paid;
import pl.mpietrewicz.sp.modules.balance.domain.balance.month.state.Underpaid;
import pl.mpietrewicz.sp.modules.balance.domain.balance.month.state.Unpaid;

@ValueObject
public abstract class MonthState {

    private final PaidStatus status;

    protected final Month month;

    private final Amount paid;

    protected MonthState(Month month, Amount paid, PaidStatus status) {
        this.month = month;
        this.paid = paid;
        this.status = status;
    }

    public abstract Amount pay(PositiveAmount payment);

    public abstract Amount refund(PositiveAmount refund);

    public abstract boolean canPaidBy(Amount payment);

    public abstract PaidStatus getPaidStatus();

    public abstract boolean isPaid();

    public abstract boolean hasPayment();

    public MonthState createCopy(Month month) {
        switch (status) {
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

    public Amount getPaid() {
        return paid;
    }

}