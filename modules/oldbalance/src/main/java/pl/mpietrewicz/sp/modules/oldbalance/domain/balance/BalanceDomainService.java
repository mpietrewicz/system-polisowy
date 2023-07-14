package pl.mpietrewicz.sp.modules.oldbalance.domain.balance;

import lombok.RequiredArgsConstructor;
import pl.mpietrewicz.sp.ddd.annotations.domain.DomainService;
import pl.mpietrewicz.sp.ddd.canonicalmodel.publishedlanguage.Frequency;
import pl.mpietrewicz.sp.ddd.canonicalmodel.publishedlanguage.snapshot.ComponentData;
import pl.mpietrewicz.sp.modules.contract.domain.contract.Contract;
import pl.mpietrewicz.sp.modules.contract.domain.premium.Premium;

import java.math.BigDecimal;
import java.time.YearMonth;
import java.util.List;

@DomainService
@RequiredArgsConstructor
public class BalanceDomainService {

    public void addComponent(Balance balance, ComponentData componentData, Premium premium) {
        BigDecimal premiumAmount = premium.getPremiumAmount(componentData.getStartDate());
        balance.addComponent(componentData, premiumAmount); // todo: trzeba przekazać całą historię!
    }

    public void openNewPeriod(Balance balance, Contract contract, YearMonth newAccountingMonth) {
        YearMonth lastPerioMonth = balance.getLastPerioMonth();
        if (lastPerioMonth.compareTo(newAccountingMonth) < 0) {
            Frequency frequency = contract.getFrequency();
            List<YearMonth> monthsToCurrentAccountingMonth = frequency.getMonths(lastPerioMonth, newAccountingMonth);
            int months = monthsToCurrentAccountingMonth.size();
            balance.addPeriods(months);
        }
    }

}