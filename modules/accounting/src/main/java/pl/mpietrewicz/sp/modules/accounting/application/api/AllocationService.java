package pl.mpietrewicz.sp.modules.accounting.application.api;

import pl.mpietrewicz.sp.ddd.canonicalmodel.publishedlanguage.AggregateId;
import pl.mpietrewicz.sp.ddd.canonicalmodel.publishedlanguage.MonthlyBalance;

import java.util.List;

public interface AllocationService {

    void update(AggregateId contractId, List<MonthlyBalance> monthlyBalances);

}