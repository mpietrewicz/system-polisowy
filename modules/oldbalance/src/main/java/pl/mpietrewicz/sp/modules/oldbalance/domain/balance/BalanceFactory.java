package pl.mpietrewicz.sp.modules.oldbalance.domain.balance;

import lombok.RequiredArgsConstructor;
import pl.mpietrewicz.sp.ddd.annotations.domain.DomainFactory;
import pl.mpietrewicz.sp.ddd.canonicalmodel.publishedlanguage.AggregateId;
import pl.mpietrewicz.sp.ddd.canonicalmodel.publishedlanguage.snapshot.ComponentData;
import pl.mpietrewicz.sp.ddd.canonicalmodel.publishedlanguage.snapshot.ContractData;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.Collections;
import java.util.Map;

import static pl.mpietrewicz.sp.modules.oldbalance.domain.balance.policy.operationcalculation.PaymentCalculationPolicyEnum.SIMPLE;
import static pl.mpietrewicz.sp.modules.oldbalance.domain.balance.policy.periodcover.PeriodCoverPolicyEnum.WITH_RENEWAL;

@DomainFactory
@RequiredArgsConstructor
public class BalanceFactory {

    private final PeriodFactory periodFactory;

    public Balance create(ContractData contractData, ComponentData componentData, Map<LocalDate, BigDecimal> premiumHistory,
                          YearMonth currentAccountingMonth) {
        AggregateId aggregateId = AggregateId.generate();
        Periods periods = periodFactory.create(contractData, componentData, premiumHistory, currentAccountingMonth);

        return new Balance(aggregateId, contractData, periods, new Operations(Collections.emptyList()));
    }

    private BalancePolicies setBalancePolicies() {
        return BalancePolicies.builder()
                .periodCoverPolicyEnum(WITH_RENEWAL)
                .paymentCalculationPolicyEnum(SIMPLE)
                .build();
    }

}