package pl.mpietrewicz.sp.modules.oldbalance.domain.balance.period;

import java.util.List;

public enum PeriodStatus {
    OVERPAID, PAID, UNDERPAID, UNPAID;

    boolean isPaid() {
        return List.of(PAID, OVERPAID).contains(this);
    }

    boolean isCovered() {
        return List.of(UNDERPAID, PAID, OVERPAID).contains(this);
    }

    boolean isNotPaid() {
        return List.of(UNPAID, UNDERPAID).contains(this);
    }

}