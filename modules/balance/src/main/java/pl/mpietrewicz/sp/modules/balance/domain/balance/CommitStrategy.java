package pl.mpietrewicz.sp.modules.balance.domain.balance;

public interface CommitStrategy {

    void commit(Operation operation);

}