package pl.mpietrewicz.sp.modules.balance.application.api.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import pl.mpietrewicz.sp.ddd.canonicalmodel.publishedlanguage.PaymentPolicyEnum;
import pl.mpietrewicz.sp.ddd.canonicalmodel.publishedlanguage.snapshot.ContractData;
import pl.mpietrewicz.sp.ddd.canonicalmodel.publishedlanguage.snapshot.PaymentData;
import pl.mpietrewicz.sp.ddd.canonicalmodel.publishedlanguage.snapshot.RefundData;
import pl.mpietrewicz.sp.ddd.canonicalmodel.publishedlanguage.snapshot.premium.PremiumSnapshot;
import pl.mpietrewicz.sp.ddd.sharedkernel.Amount;
import pl.mpietrewicz.sp.modules.balance.application.api.BalanceService;
import pl.mpietrewicz.sp.modules.balance.domain.balance.Balance;
import pl.mpietrewicz.sp.modules.balance.domain.balance.BalanceFactory;
import pl.mpietrewicz.sp.modules.balance.infrastructure.repo.BalanceRepository;

import java.time.LocalDate;

@Component
@Transactional(transactionManager = "balanceTransactionManager")
@RequiredArgsConstructor
public class BalanceServiceImpl implements BalanceService {

    private final BalanceFactory balanceFactory;
    private final BalanceRepository balanceRepository;

    @Override
    public void createBalance(ContractData contractData, PremiumSnapshot premiumSnapshot) {
        Balance balance = balanceFactory.create(contractData, premiumSnapshot);
        balanceRepository.save(balance);
    }

    @Override
    public void addPayment(PaymentData paymentData, PaymentPolicyEnum paymentPolicyEnum) {
        Balance balance = balanceRepository.findByContractIdNew(paymentData.getContractId());
        LocalDate date = paymentData.getDate();
        Amount payment = paymentData.getAmount();

        balance.addPayment(date, payment, paymentPolicyEnum);
        balanceRepository.merge(balance);
    }

    @Override
    public void addRefund(RefundData refundData) {
        Balance balance = balanceRepository.findByContractIdNew(refundData.getContractId());
        LocalDate date = refundData.getDate();
        Amount refund = refundData.getAmount();

        balance.addRefund(date, refund);
        balanceRepository.merge(balance);
    }

    @Override
    public void changePremium(LocalDate date, PremiumSnapshot premiumSnapshot) {
        ContractData contractData = premiumSnapshot.getContractData();
        Balance balance = balanceRepository.findByContractIdNew(contractData.getAggregateId());

        balance.changePremium(date, premiumSnapshot);
        balanceRepository.merge(balance);
    }

    @Override
    public void stopCalculating(LocalDate date, LocalDate end, ContractData contractData) {
        Balance balance = balanceRepository.findByContractIdNew(contractData.getAggregateId());

        balance.stopCalculating(date, end);
        balanceRepository.merge(balance);
    }

}