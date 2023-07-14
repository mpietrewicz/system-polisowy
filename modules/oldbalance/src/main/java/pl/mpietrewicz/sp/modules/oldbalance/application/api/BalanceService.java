package pl.mpietrewicz.sp.modules.oldbalance.application.api;

import pl.mpietrewicz.sp.ddd.canonicalmodel.publishedlanguage.AggregateId;
import pl.mpietrewicz.sp.ddd.canonicalmodel.publishedlanguage.snapshot.ComponentData;
import pl.mpietrewicz.sp.ddd.canonicalmodel.publishedlanguage.snapshot.PaymentData;
import pl.mpietrewicz.sp.ddd.canonicalmodel.publishedlanguage.snapshot.RefundData;

import java.time.LocalDate;
import java.time.YearMonth;

public interface BalanceService {

    void addPayment(PaymentData paymentData);

    void addRefund(RefundData refundData);

    void addComponent(ComponentData componentData);

    void terminateComponent(ComponentData componentData, LocalDate terminatedDate);

    void openNewPeriod(AggregateId contractId, YearMonth newAccountingMonth);

}