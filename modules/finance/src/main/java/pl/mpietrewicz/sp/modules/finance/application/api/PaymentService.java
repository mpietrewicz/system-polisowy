
package pl.mpietrewicz.sp.modules.finance.application.api;

import pl.mpietrewicz.sp.ddd.sharedkernel.Amount;

import java.time.LocalDate;

public interface PaymentService {

	void addPayment(String contractId, Amount amount, LocalDate date);

}