package pl.mpietrewicz.sp.modules.balance.domain.balance;

import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import pl.mpietrewicz.sp.ddd.annotations.domain.DomainFactory;
import pl.mpietrewicz.sp.ddd.canonicalmodel.publishedlanguage.AggregateId;
import pl.mpietrewicz.sp.ddd.canonicalmodel.publishedlanguage.snapshot.ContractData;
import pl.mpietrewicz.sp.ddd.canonicalmodel.publishedlanguage.snapshot.premium.PremiumSnapshot;
import pl.mpietrewicz.sp.modules.balance.domain.balance.operation.type.StartCalculating;

import javax.inject.Inject;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;

@DomainFactory
public class BalanceFactory {

    @Inject
    private AutowireCapableBeanFactory spring;

    public Balance create(ContractData contractData, PremiumSnapshot premiumSnapshot) {
        Balance balance = new Balance(AggregateId.generate(), 0L, contractData.getAggregateId());
        spring.autowireBean(balance);

        LocalDate contractStart = contractData.getStart();
        Period period = new Period(contractStart, new ArrayList<>(), true, "init");
        StartCalculating startCalculating = new StartCalculating(YearMonth.from(contractStart),
                premiumSnapshot.getAmountAt(contractStart), period, balance);

        balance.operations.add(startCalculating);
        return balance;
    }

}