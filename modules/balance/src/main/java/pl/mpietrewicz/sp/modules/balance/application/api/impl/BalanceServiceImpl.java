package pl.mpietrewicz.sp.modules.balance.application.api.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import pl.mpietrewicz.sp.ddd.canonicalmodel.publishedlanguage.AggregateId;
import pl.mpietrewicz.sp.ddd.canonicalmodel.publishedlanguage.PaymentPolicyEnum;
import pl.mpietrewicz.sp.ddd.canonicalmodel.publishedlanguage.snapshot.ComponentData;
import pl.mpietrewicz.sp.ddd.canonicalmodel.publishedlanguage.snapshot.ContractData;
import pl.mpietrewicz.sp.ddd.canonicalmodel.publishedlanguage.snapshot.PaymentData;
import pl.mpietrewicz.sp.ddd.canonicalmodel.publishedlanguage.snapshot.RefundData;
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
    public void createBalance(ContractData contractData, ComponentData componentData, Amount premium) {
        Balance balance = balanceFactory.create(contractData, componentData, premium);
        balanceRepository.save(balance);
    }

    @Override
    public void addPayment(PaymentData paymentData, PaymentPolicyEnum paymentPolicyEnum) {
        Balance balance = balanceRepository.findByContractId(paymentData.getContractId());
        LocalDate date = paymentData.getDate();
        Amount payment = paymentData.getAmount();

        balance.addPayment(date, payment, paymentPolicyEnum);
    }

    @Override
    public void addRefund(RefundData refundData) {
        Balance balance = balanceRepository.findByContractId(refundData.getContractId());
        LocalDate date = refundData.getDate();
        Amount refund = refundData.getAmount();

        balance.addRefund(date, refund);
    }

    @Override
    public void changePremium(ComponentData componentData, Amount premium) {
        Balance balance = balanceRepository.findByContractId(componentData.getContractId());
        LocalDate date = componentData.getStartDate();
        AggregateId componentId = componentData.getAggregateId();

        balance.changePremium(date, premium, componentId);
    }

}