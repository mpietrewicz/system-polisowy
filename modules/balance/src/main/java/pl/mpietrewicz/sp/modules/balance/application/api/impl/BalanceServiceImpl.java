package pl.mpietrewicz.sp.modules.balance.application.api.impl;

import lombok.RequiredArgsConstructor;
import pl.mpietrewicz.sp.ddd.annotations.application.ApplicationService;
import pl.mpietrewicz.sp.ddd.canonicalmodel.events.RefundAddedEvent;
import pl.mpietrewicz.sp.ddd.canonicalmodel.publishedlanguage.AggregateId;
import pl.mpietrewicz.sp.ddd.canonicalmodel.publishedlanguage.PaymentPolicyEnum;
import pl.mpietrewicz.sp.ddd.canonicalmodel.publishedlanguage.snapshot.ContractData;
import pl.mpietrewicz.sp.ddd.canonicalmodel.publishedlanguage.snapshot.PaymentData;
import pl.mpietrewicz.sp.ddd.canonicalmodel.publishedlanguage.snapshot.RefundData;
import pl.mpietrewicz.sp.ddd.canonicalmodel.publishedlanguage.snapshot.premium.PremiumSnapshot;
import pl.mpietrewicz.sp.ddd.sharedkernel.valueobject.Amount;
import pl.mpietrewicz.sp.ddd.support.domain.DomainEventPublisher;
import pl.mpietrewicz.sp.modules.balance.application.api.BalanceService;
import pl.mpietrewicz.sp.modules.balance.domain.balance.Balance;
import pl.mpietrewicz.sp.modules.balance.domain.balance.BalanceFactory;
import pl.mpietrewicz.sp.modules.balance.infrastructure.repo.BalanceRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;

@ApplicationService(boundedContext = "balance", transactionManager = "balanceTransactionManager")
@RequiredArgsConstructor
public class BalanceServiceImpl implements BalanceService {

    private final BalanceFactory balanceFactory;
    private final BalanceRepository balanceRepository;
    private final DomainEventPublisher eventPublisher;

    @Override
    public void createBalance(ContractData contractData, PremiumSnapshot premiumSnapshot) {
        Balance balance = balanceFactory.create(contractData, premiumSnapshot);
        balanceRepository.save(balance);
    }

    @Override
    public void addPayment(PaymentData paymentData, PaymentPolicyEnum paymentPolicyEnum) {
        Balance balance = balanceRepository.findBy(paymentData.getContractId());
        balance.addPayment(paymentData, paymentPolicyEnum);
    }

    @Override
    public void addRefund(RefundData refundData) {
        Balance balance = balanceRepository.findBy(refundData.getContractId());
        balance.addRefund(refundData);
    }

    @Override
    public void addRefund(ContractData contractData, Amount amount) {
        Balance balance = balanceRepository.findBy(contractData.getAggregateId());
        RefundData refundData = balance.addRefund(amount);

        RefundAddedEvent event = new RefundAddedEvent(refundData);
        eventPublisher.publish(event);
    }

    @Override
    public void changePremium(AggregateId contractId, LocalDate date, LocalDateTime timestamp) {
        Balance balance = balanceRepository.findBy(contractId);
        balance.changePremium(date, timestamp);
    }

    @Override
    public void stopCalculating(ContractData contractData, LocalDate end) {
        Balance balance = balanceRepository.findBy(contractData.getAggregateId());
        balance.stopCalculating(end);
    }

    @Override
    public void cancelStopCalculating(ContractData contractData) {
        Balance balance = balanceRepository.findBy(contractData.getAggregateId());
        balance.cancelStopCalculating();
    }

}