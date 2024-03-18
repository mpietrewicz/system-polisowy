package pl.mpietrewicz.sp.modules.balance.application.api.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import pl.mpietrewicz.sp.ddd.canonicalmodel.publishedlanguage.PaymentPolicyEnum;
import pl.mpietrewicz.sp.ddd.canonicalmodel.publishedlanguage.snapshot.ContractData;
import pl.mpietrewicz.sp.ddd.canonicalmodel.publishedlanguage.snapshot.PaymentData;
import pl.mpietrewicz.sp.ddd.canonicalmodel.publishedlanguage.snapshot.RefundData;
import pl.mpietrewicz.sp.ddd.canonicalmodel.publishedlanguage.snapshot.premium.PremiumSnapshot;
import pl.mpietrewicz.sp.modules.balance.application.api.BalanceService;
import pl.mpietrewicz.sp.modules.balance.domain.balance.Balance;
import pl.mpietrewicz.sp.modules.balance.domain.balance.BalanceFactory;
import pl.mpietrewicz.sp.modules.balance.infrastructure.repo.BalanceRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;

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
        Balance balance = balanceRepository.findByContractId(paymentData.getContractId());
        balance.addPayment(paymentData, paymentPolicyEnum);
    }

    @Override
    public void addRefund(RefundData refundData) {
        Balance balance = balanceRepository.findByContractId(refundData.getContractId());
        balance.addRefund(refundData);
    }

    @Override
    public void changePremium(ContractData contractData, LocalDate date, LocalDateTime timestamp) {
        Balance balance = balanceRepository.findByContractId(contractData.getAggregateId());
        balance.changePremium(date, timestamp);
    }

    @Override
    public void stopCalculating(ContractData contractData, LocalDate end) {
        Balance balance = balanceRepository.findByContractId(contractData.getAggregateId());
        balance.stopCalculating(end);
    }

    @Override
    public void cancelStopCalculating(ContractData contractData) {
        Balance balance = balanceRepository.findByContractId(contractData.getAggregateId());
        balance.cancelStopCalculating();
    }

}