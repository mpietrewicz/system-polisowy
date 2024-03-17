package pl.mpietrewicz.sp.modules.balance.exceptions;

public class ReexecutionException extends BalanceException {

    public ReexecutionException(Throwable cause, String message, Object... args) {
        super(cause, message, args);
    }

}