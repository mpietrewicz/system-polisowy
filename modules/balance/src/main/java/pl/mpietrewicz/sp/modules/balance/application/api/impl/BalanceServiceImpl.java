package pl.mpietrewicz.sp.modules.balance.application.api.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import pl.mpietrewicz.sp.ddd.canonicalmodel.publishedlanguage.AggregateId;
import pl.mpietrewicz.sp.ddd.canonicalmodel.publishedlanguage.PaymentPolicy;
import pl.mpietrewicz.sp.ddd.canonicalmodel.publishedlanguage.snapshot.ComponentData;
import pl.mpietrewicz.sp.ddd.canonicalmodel.publishedlanguage.snapshot.ContractData;
import pl.mpietrewicz.sp.ddd.canonicalmodel.publishedlanguage.snapshot.PaymentData;
import pl.mpietrewicz.sp.ddd.canonicalmodel.publishedlanguage.snapshot.RefundData;
import pl.mpietrewicz.sp.modules.balance.application.api.BalanceService;
import pl.mpietrewicz.sp.modules.balance.domain.balance.Balance;
import pl.mpietrewicz.sp.modules.balance.domain.balance.BalanceFactory;
import pl.mpietrewicz.sp.modules.balance.infrastructure.repo.BalanceRepository;

import java.math.BigDecimal;
import java.time.LocalDate;

@Component
@Transactional(transactionManager = "balanceTransactionManager")
@RequiredArgsConstructor
public class BalanceServiceImpl implements BalanceService {

    private final BalanceFactory balanceFactory;
    private final BalanceRepository balanceRepository;

    @Override
    public void createBalance(ContractData contractData, ComponentData componentData, BigDecimal premium) {
        int grace = 3; // todo: do wyniesienia wyżej
        Balance balance = balanceFactory.create(contractData, componentData, premium, grace);
        balanceRepository.save(balance);
    }

    @Override
    public void addPayment(PaymentData paymentData, PaymentPolicy paymentPolicy) {
        Balance balance = balanceRepository.findByContractId(paymentData.getContractId());
        LocalDate date = paymentData.getDate();
        BigDecimal amount = paymentData.getAmount();

        balance.addPayment(date, amount, paymentPolicy);
    }

    @Override
    public void addRefund(RefundData refundData) {
        Balance balance = balanceRepository.findByContractId(refundData.getContractId());
        LocalDate date = refundData.getDate();
        BigDecimal amount = refundData.getAmount();

        balance.addRefund(date, amount);
    }

    @Override
    public void changePremium(ComponentData componentData, BigDecimal premium) {
        Balance balance = balanceRepository.findByContractId(componentData.getContractId());
        LocalDate date = componentData.getStartDate();
        AggregateId componentId = componentData.getAggregateId();

        balance.changePremium(date, premium, componentId);
    }

}