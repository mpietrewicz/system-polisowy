package pl.mpietrewicz.sp.modules.oldbalance.application.api.impl;

import lombok.RequiredArgsConstructor;
import pl.mpietrewicz.sp.ddd.annotations.application.ApplicationService;
import pl.mpietrewicz.sp.ddd.canonicalmodel.publishedlanguage.AggregateId;
import pl.mpietrewicz.sp.ddd.canonicalmodel.publishedlanguage.snapshot.ComponentData;
import pl.mpietrewicz.sp.ddd.canonicalmodel.publishedlanguage.snapshot.PaymentData;
import pl.mpietrewicz.sp.ddd.canonicalmodel.publishedlanguage.snapshot.RefundData;
import pl.mpietrewicz.sp.modules.contract.domain.contract.Contract;
import pl.mpietrewicz.sp.modules.contract.domain.contract.ContractRepository;
import pl.mpietrewicz.sp.modules.contract.domain.premium.Premium;
import pl.mpietrewicz.sp.modules.contract.domain.premium.PremiumRepository;
import pl.mpietrewicz.sp.modules.oldbalance.application.api.BalanceService;
import pl.mpietrewicz.sp.modules.oldbalance.domain.balance.Balance;
import pl.mpietrewicz.sp.modules.oldbalance.domain.balance.BalanceDomainService;
import pl.mpietrewicz.sp.modules.oldbalance.domain.balance.BalanceRepository;

import java.time.LocalDate;
import java.time.YearMonth;

@ApplicationService
@RequiredArgsConstructor
public class BalanceServiceImpl implements BalanceService {

    private final BalanceRepository balanceRepository;
    private final BalanceDomainService balanceDomainService;
    private final PremiumRepository premiumRepository;
    private final ContractRepository contractRepository;

    @Override
    public void addPayment(PaymentData paymentData) {
        Balance balance = balanceRepository.findByContractId(paymentData.getContractId());
        balance.addPayment(paymentData);
    }

    @Override
    public void addRefund(RefundData refundData) {
        Balance balance = balanceRepository.findByContractId(refundData.getContractId());
        balance.addRefund(refundData);
    }

    @Override
    public void addComponent(ComponentData componentData) {
        Balance balance = balanceRepository.findByContractId(componentData.getContractId());
        Premium premium = premiumRepository.findByComponentId(componentData.getAggregateId());

        balanceDomainService.addComponent(balance, componentData, premium);
    }

    @Override
    public void terminateComponent(ComponentData componentData, LocalDate terminatedDate) {
        Balance balance = balanceRepository.findByContractId(componentData.getAggregateId());
        balance.terminateComponent(componentData, terminatedDate);
    }

    @Override
    public void openNewPeriod(AggregateId contractId, YearMonth newAccountingMonth) {
        Balance balance = balanceRepository.findByContractId(contractId);
        Contract contract = contractRepository.load(contractId);

        balanceDomainService.openNewPeriod(balance, contract, newAccountingMonth);
    }

}