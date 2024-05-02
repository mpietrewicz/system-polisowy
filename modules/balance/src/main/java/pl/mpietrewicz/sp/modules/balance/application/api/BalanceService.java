package pl.mpietrewicz.sp.modules.balance.application.api;

import pl.mpietrewicz.sp.ddd.canonicalmodel.publishedlanguage.AggregateId;
import pl.mpietrewicz.sp.ddd.canonicalmodel.publishedlanguage.PaymentPolicyEnum;
import pl.mpietrewicz.sp.ddd.canonicalmodel.publishedlanguage.snapshot.ContractData;
import pl.mpietrewicz.sp.ddd.canonicalmodel.publishedlanguage.snapshot.PaymentData;
import pl.mpietrewicz.sp.ddd.canonicalmodel.publishedlanguage.snapshot.RefundData;
import pl.mpietrewicz.sp.ddd.canonicalmodel.publishedlanguage.snapshot.premium.PremiumSnapshot;
import pl.mpietrewicz.sp.ddd.sharedkernel.valueobject.PositiveAmount;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.Map;

public interface BalanceService {

    void createBalance(ContractData contractData, PremiumSnapshot premiumSnapshot);

    void addPayment(PaymentData paymentData, PaymentPolicyEnum paymentPolicyEnum);

    void addRefund(RefundData refundData);

    void addRefund(AggregateId contractId, PositiveAmount refund);

    void changePremium(AggregateId contractId, LocalDate date, LocalDateTime timestamp);

    void stopCalculating(ContractData contractData, LocalDate end);

    void cancelStopCalculating(ContractData contractData);

    Map<YearMonth, BigDecimal> getPaidTo(AggregateId contractId);

}