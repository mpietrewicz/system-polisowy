package pl.mpietrewicz.sp.modules.balance.domain.balance.month;

import pl.mpietrewicz.sp.ddd.annotations.domain.ValueObject;

@ValueObject
public enum PaidStatus {

    OVERPAID, PAID, UNDERPAID, UNPAID;

}