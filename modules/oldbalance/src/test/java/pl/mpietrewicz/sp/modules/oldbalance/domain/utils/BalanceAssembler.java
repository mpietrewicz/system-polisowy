package pl.mpietrewicz.sp.modules.oldbalance.domain.utils;

import pl.mpietrewicz.sp.modules.oldbalance.domain.balance.Balance;
import pl.mpietrewicz.sp.modules.oldbalance.domain.balance.Operations;
import pl.mpietrewicz.sp.modules.oldbalance.domain.balance.Periods;
import pl.mpietrewicz.sp.modules.oldbalance.domain.balance.operation.Payment;
import pl.mpietrewicz.sp.modules.oldbalance.domain.balance.period.Period;
import pl.mpietrewicz.sp.modules.oldbalance.domain.balance.policy.PeriodCoverPolicy;
import pl.mpietrewicz.sp.modules.oldbalance.domain.balance.policy.periodcover.WithRenewal;
import pl.mpietrewicz.sp.ddd.canonicalmodel.publishedlanguage.AggregateId;
import pl.mpietrewicz.sp.ddd.canonicalmodel.publishedlanguage.snapshot.ContractData;

import java.util.ArrayList;
import java.util.List;

public class BalanceAssembler {

    private ContractData contractData = new ContractData(AggregateId.generate(), null, null);
    private Periods periods;
    private Operations operations = new Operations(new ArrayList<>());

    public BalanceAssembler withPeriods(List<Period> periods) {
        for (int i = 0; i < periods.size()-1; i++) {
            periods.get(i).setNext(periods.get(i+1));
            periods.get(i+1).setPervious(periods.get(i));
        }
        PeriodCoverPolicy periodCoverPolicy = new WithRenewal();
        this.periods = new Periods(periods, periodCoverPolicy);
        return this;
    }

    public BalanceAssembler withFunds(List<Payment> payments) {
        this.operations =  new Operations();
        operations.addAll(payments);
        return this;
    }

    public Balance build() {
        return new Balance(AggregateId.generate(), contractData, periods, operations);
    }

}