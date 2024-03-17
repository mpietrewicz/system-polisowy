package pl.mpietrewicz.sp.modules.balance.exceptions;

public class NoOperationException extends BalanceException {

    public NoOperationException(Throwable cause, String message, Object... args) {
        super(cause, message, args);
    }

}