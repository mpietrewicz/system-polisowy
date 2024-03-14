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
    public void changePremium(ContractData contractData, LocalDate date, LocalDateTime timestamp) {
        Balance balance = balanceRepository.findByContractIdNew(contractData.getAggregateId());

        balance.changePremium(date, timestamp);
        balanceRepository.merge(balance);
    }

    @Override
    public void stopCalculating(LocalDate end, ContractData contractData) {
        Balance balance = balanceRepository.findByContractIdNew(contractData.getAggregateId());

        balance.stopCalculating(end);
        balanceRepository.merge(balance);
    }

    @Override
    public void cancelStopCalculating(ContractData contractData) {
        Balance balance = balanceRepository.findByContractIdNew(contractData.getAggregateId());

        balance.cancelStopCalculating();
        balanceRepository.merge(balance);
    }

    @Override
    public void getBalance(ContractData contractData) {
        Balance balance = balanceRepository.findByContractIdNew(contractData.getAggregateId());
        System.out.println(balance);
    }

}