package pl.mpietrewicz.sp.modules.balance.domain.balance.migration;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Builder
@Getter
public class BalanceMigration {

    private final String contractId;
    private final List<BalanceOperation> balanceOperations;

}