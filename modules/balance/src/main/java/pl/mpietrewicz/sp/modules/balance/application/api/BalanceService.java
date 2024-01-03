package pl.mpietrewicz.sp.modules.balance.application.api;

import pl.mpietrewicz.sp.ddd.canonicalmodel.publishedlanguage.PaymentPolicyEnum;
import pl.mpietrewicz.sp.ddd.canonicalmodel.publishedlanguage.snapshot.ComponentData;
import pl.mpietrewicz.sp.ddd.canonicalmodel.publishedlanguage.snapshot.ContractData;
import pl.mpietrewicz.sp.ddd.canonicalmodel.publishedlanguage.snapshot.PaymentData;
import pl.mpietrewicz.sp.ddd.canonicalmodel.publishedlanguage.snapshot.RefundData;
import pl.mpietrewicz.sp.ddd.sharedkernel.Amount;

public interface BalanceService {

    void createBalance(ContractData contractData, ComponentData componentData, Amount premium);

    void addPayment(PaymentData paymentData, PaymentPolicyEnum paymentPolicyEnum);

    void addRefund(RefundData refundData);

    void changePremium(ComponentData componentData, Amount premium);

}