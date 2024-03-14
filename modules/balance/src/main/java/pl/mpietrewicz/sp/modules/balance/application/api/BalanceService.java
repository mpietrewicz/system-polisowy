package pl.mpietrewicz.sp.modules.balance.application.api;

import pl.mpietrewicz.sp.ddd.canonicalmodel.publishedlanguage.PaymentPolicyEnum;
import pl.mpietrewicz.sp.ddd.canonicalmodel.publishedlanguage.snapshot.ContractData;
import pl.mpietrewicz.sp.ddd.canonicalmodel.publishedlanguage.snapshot.PaymentData;
import pl.mpietrewicz.sp.ddd.canonicalmodel.publishedlanguage.snapshot.premium.PremiumSnapshot;
import pl.mpietrewicz.sp.ddd.canonicalmodel.publishedlanguage.snapshot.RefundData;

import java.time.LocalDate;
import java.time.LocalDateTime;

public interface BalanceService {

    void createBalance(ContractData contractData, PremiumSnapshot premiumSnapshot);

    void addPayment(PaymentData paymentData, PaymentPolicyEnum paymentPolicyEnum);

    void addRefund(RefundData refundData);

    void changePremium(ContractData contractData, LocalDate date, LocalDateTime timestamp);

    void stopCalculating(LocalDate end, ContractData contractData); // todo: podpiąć pod dobry event

    void cancelStopCalculating(ContractData contractData); // todo: podpiąć pod dobry event

    void getBalance(ContractData contractData); // todo: do usunięcia w przyszłości

}