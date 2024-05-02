
package pl.mpietrewicz.sp.modules.finance.application.api;

import pl.mpietrewicz.sp.ddd.canonicalmodel.publishedlanguage.AggregateId;
import pl.mpietrewicz.sp.ddd.canonicalmodel.publishedlanguage.snapshot.RefundData;
import pl.mpietrewicz.sp.ddd.sharedkernel.valueobject.PositiveAmount;

import java.time.LocalDate;

public interface FinanceService {

	void addPayment(AggregateId contractId, PositiveAmount payment, LocalDate date);

	void refundPayment(AggregateId paymentId);

	void addSubsidy(AggregateId contractId, PositiveAmount subsidy, LocalDate date);

	void addRefund(RefundData refundData);

}