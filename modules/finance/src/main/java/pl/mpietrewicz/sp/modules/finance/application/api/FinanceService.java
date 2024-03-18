
package pl.mpietrewicz.sp.modules.finance.application.api;

import pl.mpietrewicz.sp.ddd.canonicalmodel.publishedlanguage.AggregateId;
import pl.mpietrewicz.sp.ddd.canonicalmodel.publishedlanguage.snapshot.RefundData;
import pl.mpietrewicz.sp.ddd.sharedkernel.valueobject.Amount;

import java.time.LocalDate;

public interface FinanceService {

	void addPayment(AggregateId contractId, Amount amount, LocalDate date);

	void refundPayment(AggregateId paymentId);

	void addFunding(AggregateId contractId, Amount amount, LocalDate date);

	void addRefund(RefundData refundData);

}