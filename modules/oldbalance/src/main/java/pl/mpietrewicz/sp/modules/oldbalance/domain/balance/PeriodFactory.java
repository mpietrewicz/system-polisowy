package pl.mpietrewicz.sp.modules.oldbalance.domain.balance;

import lombok.RequiredArgsConstructor;
import pl.mpietrewicz.sp.ddd.annotations.domain.DomainFactory;
import pl.mpietrewicz.sp.ddd.canonicalmodel.publishedlanguage.Frequency;
import pl.mpietrewicz.sp.ddd.canonicalmodel.publishedlanguage.snapshot.ComponentData;
import pl.mpietrewicz.sp.ddd.canonicalmodel.publishedlanguage.snapshot.ContractData;
import pl.mpietrewicz.sp.modules.oldbalance.domain.balance.period.Period;
import pl.mpietrewicz.sp.modules.oldbalance.domain.balance.policy.PeriodCoverPolicy;
import pl.mpietrewicz.sp.modules.oldbalance.domain.balance.policy.periodcover.PeriodCoverPolicyFactory;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static java.math.BigDecimal.ZERO;
import static pl.mpietrewicz.sp.modules.oldbalance.domain.balance.period.PeriodStatus.UNPAID;
import static pl.mpietrewicz.sp.modules.oldbalance.domain.balance.policy.periodcover.PeriodCoverPolicyEnum.WITH_RENEWAL;

@DomainFactory
@RequiredArgsConstructor
public class PeriodFactory {

    private final PeriodCoverPolicyFactory periodCoverPolicyFactory;

    public Periods create(ContractData contractData, ComponentData componentData, Map<LocalDate, BigDecimal> premiumHistory,
                          YearMonth currentAccountingMonth) {
        YearMonth startOf = YearMonth.from(contractData.getContractStartDate());
        PeriodCoverPolicy periodCoverPolicy = periodCoverPolicyFactory.create(WITH_RENEWAL);
        return new Periods(createPeriods(startOf, currentAccountingMonth, contractData,
                componentData, premiumHistory), periodCoverPolicy);
    }

    public List<Period> createPeriods(YearMonth  startOf, YearMonth currentAccountingMonth, ContractData contractData,
                                      ComponentData componentData, Map<LocalDate, BigDecimal> premiumHistory) {
        Frequency frequency = contractData.getFrequency();

        List<YearMonth> monthsToCurrentAccountingMonth = frequency.getMonths(startOf, currentAccountingMonth);

        Period period = null;
        List<Period> periods = new ArrayList<>();
        for (YearMonth month : monthsToCurrentAccountingMonth) {
            BigDecimal premium = getPremiumAt(month, premiumHistory);
            if (period == null) {
                period = createPeriod(month, componentData, premium);
            } else {
                Period next = createPeriod(month, componentData, premium, period);
                period.setNext(next);
                period = next;
            }
            periods.add(period);
        }
        return periods;
    }

    private BigDecimal getPremiumAt(YearMonth month, Map<LocalDate, BigDecimal> premiumHistory) {
        LocalDate lastPremiumChange = premiumHistory.keySet().stream()
                .filter(date -> YearMonth.from(date).compareTo(month) <= 0)
                .max(LocalDate::compareTo)
                .orElseThrow();
        return premiumHistory.get(lastPremiumChange);
    }

    private Period createPeriod(YearMonth month, ComponentData componentData, BigDecimal premium) {
        ComponentPremiumDue componentPremiumDue = new ComponentPremiumDue(componentData, premium);
        PremiumDue premiumDue = new PremiumDue(Arrays.asList(componentPremiumDue)); // todo: w przyszłosci może być kilka składników zakładanych na raz!
        return new Period(month, premiumDue, UNPAID, ZERO, ZERO);
    }

    private Period createPeriod(YearMonth month, ComponentData componentData, BigDecimal premium, Period previous) {
        ComponentPremiumDue componentPremiumDue = new ComponentPremiumDue(componentData, premium);
        PremiumDue premiumDue = new PremiumDue(Arrays.asList(componentPremiumDue)); // todo: w przyszłosci może być kilka składników zakładanych na raz!
        return new Period(month, premiumDue, UNPAID, ZERO, ZERO,previous);
    }
}