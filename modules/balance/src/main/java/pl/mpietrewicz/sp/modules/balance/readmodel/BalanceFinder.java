package pl.mpietrewicz.sp.modules.balance.readmodel;

import lombok.RequiredArgsConstructor;
import pl.mpietrewicz.sp.ddd.annotations.application.Finder;
import pl.mpietrewicz.sp.ddd.canonicalmodel.publishedlanguage.AggregateId;
import pl.mpietrewicz.sp.modules.balance.application.api.BalanceService;

import java.math.BigDecimal;
import java.time.YearMonth;
import java.util.Map;

@Finder
@RequiredArgsConstructor
public class BalanceFinder {

    private final BalanceService balanceService;

    public Map<YearMonth, BigDecimal> findPaidTo(AggregateId contractId) {
        return balanceService.getPaidTo(contractId);
    }

}