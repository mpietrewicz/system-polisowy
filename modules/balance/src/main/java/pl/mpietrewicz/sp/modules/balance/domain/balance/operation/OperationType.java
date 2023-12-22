package pl.mpietrewicz.sp.modules.balance.domain.balance.operation;

import pl.mpietrewicz.sp.ddd.annotations.domain.ValueObject;

@ValueObject
public enum OperationType {

    START_CALCULATING, ADD_PAYMENT, ADD_REFUND, CHANGE_PREMIUM,
    STOP_CALCULATING, CANCEL_STOP_CALCULATING;

}