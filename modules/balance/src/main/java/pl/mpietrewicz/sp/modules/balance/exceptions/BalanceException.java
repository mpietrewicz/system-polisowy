package pl.mpietrewicz.sp.modules.balance.exceptions;

public class BalanceException extends Exception {

    public BalanceException(String message) {
        super(message);
    }

    public BalanceException(String message, Throwable cause) {
        super(message, cause);
    }

}