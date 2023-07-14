package pl.mpietrewicz.sp.modules.balance.domain.balance.month;

import java.util.List;

public enum MonthStatus {
    OVERPAID, PAID, UNDERPAID, UNPAID;

    boolean isPaid() {
        return List.of(PAID, OVERPAID).contains(this);
    }

    boolean isNotPaid() {
        return List.of(UNPAID, UNDERPAID).contains(this);
    }

}