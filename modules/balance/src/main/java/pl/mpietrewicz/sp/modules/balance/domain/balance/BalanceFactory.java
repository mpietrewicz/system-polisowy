package pl.mpietrewicz.sp.modules.balance.domain.balance;

import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import pl.mpietrewicz.sp.ddd.annotations.domain.DomainFactory;
import pl.mpietrewicz.sp.ddd.canonicalmodel.publishedlanguage.AggregateId;
import pl.mpietrewicz.sp.ddd.canonicalmodel.publishedlanguage.snapshot.ContractData;
import pl.mpietrewicz.sp.ddd.canonicalmodel.publishedlanguage.snapshot.premium.PremiumSnapshot;

import javax.inject.Inject;
import java.time.LocalDate;

@DomainFactory
public class BalanceFactory {

    @Inject
    private AutowireCapableBeanFactory spring;

    public Balance create(ContractData contractData, PremiumSnapshot premiumSnapshot) {
        Balance balance = new Balance(AggregateId.generate(), contractData);
        spring.autowireBean(balance);

        LocalDate start = contractData.getContractStartDate();
        balance.startCalculating(start, premiumSnapshot);
        return balance;
    }

}