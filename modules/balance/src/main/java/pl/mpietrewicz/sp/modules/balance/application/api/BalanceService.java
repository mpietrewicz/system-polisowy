package pl.mpietrewicz.sp.modules.balance.application.api;

import pl.mpietrewicz.sp.ddd.canonicalmodel.publishedlanguage.PaymentPolicy;
import pl.mpietrewicz.sp.ddd.canonicalmodel.publishedlanguage.snapshot.ComponentData;
import pl.mpietrewicz.sp.ddd.canonicalmodel.publishedlanguage.snapshot.ContractData;
import pl.mpietrewicz.sp.ddd.canonicalmodel.publishedlanguage.snapshot.PaymentData;
import pl.mpietrewicz.sp.ddd.canonicalmodel.publishedlanguage.snapshot.RefundData;

import java.math.BigDecimal;

public interface BalanceService {

    void createBalance(ContractData contractData, ComponentData componentData, BigDecimal premium);

    void addPayment(PaymentData paymentData, PaymentPolicy paymentPolicy);

    void addRefund(RefundData refundData);

    void changePremium(ComponentData componentData, BigDecimal premium);

}