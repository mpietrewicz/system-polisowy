package pl.mpietrewicz.sp.modules.balance.application.api.impl;

import io.micrometer.core.annotation.Timed;
import lombok.RequiredArgsConstructor;
import pl.mpietrewicz.sp.ddd.annotations.application.ApplicationService;
import pl.mpietrewicz.sp.ddd.canonicalmodel.events.RefundAddedEvent;
import pl.mpietrewicz.sp.ddd.canonicalmodel.publishedlanguage.AggregateId;
import pl.mpietrewicz.sp.ddd.canonicalmodel.publishedlanguage.PaymentPolicyEnum;
import pl.mpietrewicz.sp.ddd.canonicalmodel.publishedlanguage.snapshot.ContractData;
import pl.mpietrewicz.sp.ddd.canonicalmodel.publishedlanguage.snapshot.PaymentData;
import pl.mpietrewicz.sp.ddd.canonicalmodel.publishedlanguage.snapshot.RefundData;
import pl.mpietrewicz.sp.ddd.canonicalmodel.publishedlanguage.snapshot.premium.PremiumSnapshot;
import pl.mpietrewicz.sp.ddd.sharedkernel.valueobject.PositiveAmount;
import pl.mpietrewicz.sp.ddd.support.domain.DomainEventPublisher;
import pl.mpietrewicz.sp.modules.balance.application.api.BalanceService;
import pl.mpietrewicz.sp.modules.balance.domain.balance.Balance;
import pl.mpietrewicz.sp.modules.balance.domain.balance.BalanceFactory;
import pl.mpietrewicz.sp.modules.balance.infrastructure.repo.BalanceRepository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.Map;

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
    @Timed(value = "BalanceService.addPayment")
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
    public void addRefund(AggregateId contractId, PositiveAmount refund) {
        Balance balance = balanceRepository.findBy(contractId);
        RefundData refundData = balance.addRefund(refund);

        RefundAddedEvent event = new RefundAddedEvent(refundData);
        eventPublisher.publish(event);
    }

    @Override
    @Timed(value = "BalanceService.changePremium")
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

    @Override
    public Map<YearMonth, BigDecimal> getPaidTo(AggregateId contractId) {
        Balance balance = balanceRepository.findBy(contractId);
        return balance.getPaidTo();
    }

}