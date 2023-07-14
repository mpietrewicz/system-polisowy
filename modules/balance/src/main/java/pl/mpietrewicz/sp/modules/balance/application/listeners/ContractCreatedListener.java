package pl.mpietrewicz.sp.modules.balance.application.listeners;

import lombok.RequiredArgsConstructor;
import pl.mpietrewicz.sp.SystemParameters;
import pl.mpietrewicz.sp.ddd.annotations.event.EventListener;
import pl.mpietrewicz.sp.ddd.annotations.event.EventListeners;
import pl.mpietrewicz.sp.ddd.canonicalmodel.events.ContractCreatedEvent;
import pl.mpietrewicz.sp.ddd.canonicalmodel.publishedlanguage.snapshot.ContractData;
import pl.mpietrewicz.sp.modules.balance.domain.balance.Balance;
import pl.mpietrewicz.sp.modules.balance.domain.balance.BalanceFactory;
import pl.mpietrewicz.sp.modules.balance.domain.balance.BalanceRepository;
import pl.mpietrewicz.sp.ddd.canonicalmodel.publishedlanguage.Frequency;

import java.math.BigDecimal;
import java.time.YearMonth;

@EventListeners
@RequiredArgsConstructor
public class ContractCreatedListener {

    private final BalanceFactory balanceFactory;
    private final BalanceRepository balanceRepository;

    @EventListener
    public void handle(ContractCreatedEvent event) {
        ContractData contractData = event.getContractData();
        YearMonth accountingMonth = SystemParameters.CURRENT_ACCOUNTING_MONTH;
        BigDecimal premium = event.getPremium();
        Frequency frequency = event.getFrequency();

        Balance balance = balanceFactory.create(contractData, accountingMonth, premium, frequency);
        balanceRepository.save(balance);
    }

}