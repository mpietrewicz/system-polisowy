package pl.mpietrewicz.sp.modules.balance.application.api;

import pl.mpietrewicz.sp.ddd.canonicalmodel.publishedlanguage.AggregateId;
import pl.mpietrewicz.sp.ddd.canonicalmodel.publishedlanguage.snapshot.ComponentData;
import pl.mpietrewicz.sp.ddd.canonicalmodel.publishedlanguage.snapshot.PaymentData;
import pl.mpietrewicz.sp.ddd.canonicalmodel.publishedlanguage.snapshot.RefundData;
import pl.mpietrewicz.sp.ddd.canonicalmodel.publishedlanguage.PaymentPolicy;

import java.math.BigDecimal;
import java.time.YearMonth;

public interface BalanceService {

    void addPayment(PaymentData paymentData, PaymentPolicy paymentPolicy);

    void addRefund(RefundData refundData);

    void openNewMonth(AggregateId contractId, YearMonth newAccountingMonth);

    void addPremium(ComponentData componentData, BigDecimal premium);

}