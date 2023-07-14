package pl.mpietrewicz.sp.modules.balance.domain.balance;

import pl.mpietrewicz.sp.ddd.annotations.domain.DomainFactory;
import pl.mpietrewicz.sp.ddd.canonicalmodel.publishedlanguage.AggregateId;
import pl.mpietrewicz.sp.ddd.canonicalmodel.publishedlanguage.Frequency;
import pl.mpietrewicz.sp.ddd.canonicalmodel.publishedlanguage.snapshot.ContractData;
import pl.mpietrewicz.sp.modules.balance.domain.balance.migration.BalanceMigration;
import pl.mpietrewicz.sp.modules.balance.domain.balance.migration.BalanceOperation;
import pl.mpietrewicz.sp.modules.balance.domain.balance.migration.OperationType;
import pl.mpietrewicz.sp.modules.balance.domain.balance.operation.StartCalculating;

import java.math.BigDecimal;
import java.time.YearMonth;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@DomainFactory
public class BalanceFactory {

    public Balance create(ContractData contractData, YearMonth accountingMonth, BigDecimal premium, Frequency frequency) {
        List<Operation> operations = new ArrayList<>();
        List<Operation> pendingOperations = new ArrayList<>();

        YearMonth contractStart = YearMonth.from(contractData.getContractStartDate());
        StartCalculating startCalculating = new StartCalculating(contractStart, premium, frequency);
        pendingOperations.add(startCalculating);

        Balance balance = new Balance(AggregateId.generate(), contractData, operations, pendingOperations);
        openMonths(balance, contractStart, accountingMonth);
        return balance;
    }

    public Balance create(ContractData contractData, YearMonth accountingMonth, List<Operation> pendingOperations) {
        List<Operation> operations = new ArrayList<>();
        Balance balance = new Balance(AggregateId.generate(), contractData, operations, pendingOperations);

        Optional<YearMonth> contractStart = pendingOperations.stream()
                .filter(StartCalculating.class::isInstance)
                .map(Operation::getDate)
                .map(YearMonth::from)
                .findAny();
        contractStart.ifPresent(month -> openMonths(balance, month, accountingMonth));
        return balance;
    }

    public Balance create(ContractData contractData, YearMonth accountingMonth, BalanceMigration balanceMigration) {
        Balance balance = new Balance(AggregateId.generate(), contractData, new ArrayList<>(), new ArrayList<>());

        Optional<YearMonth> contractStart = balanceMigration.getBalanceOperations().stream()
                .filter(balanceOperation -> balanceOperation.getOperationType() == OperationType.START_CONTRACT)
                .findAny()
                .map(BalanceOperation::getDate)
                .map(YearMonth::from);
        // todo: dokończyć wywoływanie poszczególnych operacji z BalanceOperation
        contractStart.ifPresent(month -> openMonths(balance, month, accountingMonth));
        return balance;
    }

    private void openMonths(Balance balance, YearMonth contractStart, YearMonth accountingMonth) {
        int months = getMonthsBetween(contractStart, accountingMonth);
        for (int i = 0; i <= months; i++) {
            balance.openMonth(contractStart.plusMonths(i));
        }
    }

    private int getMonthsBetween(YearMonth from, YearMonth to) {
        return Long.valueOf(ChronoUnit.MONTHS.between(from, to)).intValue();
    }

}