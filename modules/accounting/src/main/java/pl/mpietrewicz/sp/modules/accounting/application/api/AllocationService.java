package pl.mpietrewicz.sp.modules.accounting.application.api;

import pl.mpietrewicz.sp.ddd.canonicalmodel.publishedlanguage.MonthlyBalance;
import pl.mpietrewicz.sp.ddd.canonicalmodel.publishedlanguage.snapshot.ContractData;

import java.util.List;

public interface AllocationService {

    void update(ContractData contractData, List<MonthlyBalance> monthlyBalances);

}