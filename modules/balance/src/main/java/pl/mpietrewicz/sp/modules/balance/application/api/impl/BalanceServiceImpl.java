package pl.mpietrewicz.sp.modules.balance.application.api.impl;

import lombok.RequiredArgsConstructor;
import pl.mpietrewicz.sp.ddd.annotations.application.ApplicationService;
import pl.mpietrewicz.sp.ddd.canonicalmodel.publishedlanguage.AggregateId;
import pl.mpietrewicz.sp.ddd.canonicalmodel.publishedlanguage.snapshot.ComponentData;
import pl.mpietrewicz.sp.ddd.canonicalmodel.publishedlanguage.snapshot.PaymentData;
import pl.mpietrewicz.sp.ddd.canonicalmodel.publishedlanguage.snapshot.RefundData;
import pl.mpietrewicz.sp.modules.balance.application.api.BalanceService;
import pl.mpietrewicz.sp.modules.balance.domain.balance.Balance;
import pl.mpietrewicz.sp.modules.balance.domain.balance.BalanceRepository;
import pl.mpietrewicz.sp.ddd.sharedkernel.PaymentPolicyEnum;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;

@ApplicationService
@RequiredArgsConstructor
public class BalanceServiceImpl implements BalanceService {

    private final BalanceRepository balanceRepository;

    @Override
    public void addPayment(PaymentData paymentData, PaymentPolicyEnum paymentPolicyEnum) {
        Balance balance = balanceRepository.findByContractId(paymentData.getContractId());
        LocalDate date = paymentData.getDate();
        BigDecimal amount = paymentData.getAmount();

        balance.addPayment(date, amount, paymentPolicyEnum);
    }

    @Override
    public void addRefund(RefundData refundData) {
        Balance balance = balanceRepository.findByContractId(refundData.getContractId());
        LocalDate date = refundData.getDate();
        BigDecimal amount = refundData.getAmount();

        balance.addRefund(date, amount);
    }

    @Override
    public void openNewMonth(AggregateId contractId, YearMonth newAccountingMonth) {
        Balance balance = balanceRepository.findByContractId(contractId);

        balance.openMonth(newAccountingMonth);
    }

    @Override
    public void addPremium(ComponentData componentData, BigDecimal premium) {
        Balance balance = balanceRepository.findByContractId(componentData.getContractId());
        LocalDate date = componentData.getStartDate();

        balance.addPremium(date, premium);
    }

}