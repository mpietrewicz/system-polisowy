package pl.mpietrewicz.sp.modules.balance.exceptions;

public class NoMonthsToRefundException extends RefundException {

    public NoMonthsToRefundException(String message) {
        super(message);
    }

}