package pl.mpietrewicz.sp.modules.balance.exceptions;

public class BalanceException extends Exception {

    public BalanceException(String message) {
        super(message);
    }

    public BalanceException(Throwable cause, String message, Object... args) {
        super(String.format(message, args), cause);
    }

}