package pl.mpietrewicz.sp.app.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pl.mpietrewicz.sp.app.readmodel.model.Result;
import pl.mpietrewicz.sp.ddd.canonicalmodel.publishedlanguage.AggregateId;
import pl.mpietrewicz.sp.modules.balance.application.api.BalanceService;

import java.math.BigDecimal;
import java.time.YearMonth;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CalculationResultPreparation {

    private final BalanceService balanceService;

    public Result prepare(AggregateId contractId) {
        Map<YearMonth, BigDecimal> paidTo = balanceService.getPaidTo(contractId);
        Optional<Map.Entry<YearMonth, BigDecimal>> any = paidTo.entrySet().stream().findAny();

        return Result.builder()
                .contractId(contractId.getId())
                .paidTo(any.map(Map.Entry::getKey).orElse(null))
                .excess(any.map(Map.Entry::getValue).orElse(null))
                .build();
    }

}