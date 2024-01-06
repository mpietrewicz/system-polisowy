package pl.mpietrewicz.sp.modules.balance.domain.balance;

import java.util.Arrays;

public enum OperationEnum {

    START_CONTRACT("ZUM"),
    PAYMENT("Wpl"),
    INCREASE_INSURANCE_SUM("PSU");

    String code;

    OperationEnum(String code) {
        this.code = code;
    }

    public static OperationEnum get(String code) {
        return Arrays.stream(OperationEnum.values())
                .filter(operationEnum -> operationEnum.code.equals(code))
                .findAny()
                .orElseThrow();
    }

}