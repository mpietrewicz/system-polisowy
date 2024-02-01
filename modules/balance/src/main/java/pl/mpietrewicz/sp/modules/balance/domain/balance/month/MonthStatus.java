package pl.mpietrewicz.sp.modules.balance.domain.balance.month;

import pl.mpietrewicz.sp.ddd.annotations.domain.ValueObject;

import java.util.List;

@ValueObject
public enum MonthStatus { // todo: to powinno być widoczne na zewnatrz?
    OVERPAID, PAID, UNDERPAID, UNPAID;

    boolean isPaid() {
        return List.of(PAID, OVERPAID).contains(this);
    }

    boolean hasPayment() {
        return List.of(OVERPAID, PAID, UNDERPAID).contains(this);
    }

    boolean isUnpaid() {
        return UNPAID == this;
    }

}