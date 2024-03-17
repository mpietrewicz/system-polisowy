package pl.mpietrewicz.sp.modules.balance.exceptions;

public class BalanceStoppedException extends BalanceException {

    public BalanceStoppedException() {
        super("Balance is currently stopped!");
    }

}