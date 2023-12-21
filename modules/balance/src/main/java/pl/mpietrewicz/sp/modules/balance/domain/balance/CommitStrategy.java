package pl.mpietrewicz.sp.modules.balance.domain.balance;

import pl.mpietrewicz.sp.modules.balance.domain.balance.operation.Operation;

public interface CommitStrategy {

    void commit(Operation operation);

}