
package pl.mpietrewicz.sp.modules.finance.application.api;

import java.math.BigDecimal;
import java.time.LocalDate;

public interface PaymentService {

	void addPayment(String contractId, BigDecimal amount, LocalDate date);

}