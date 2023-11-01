
package pl.mpietrewicz.sp.modules.contract.application.api;


import pl.mpietrewicz.sp.ddd.canonicalmodel.publishedlanguage.AggregateId;
import pl.mpietrewicz.sp.ddd.canonicalmodel.publishedlanguage.Frequency;
import pl.mpietrewicz.sp.ddd.canonicalmodel.publishedlanguage.PaymentPolicy;
import pl.mpietrewicz.sp.ddd.canonicalmodel.publishedlanguage.snapshot.ContractData;
import pl.mpietrewicz.sp.modules.contract.domain.contract.Contract;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;

public interface ContractService {

	Contract createContract(LocalDate registerDate, BigDecimal premium, Frequency frequency, PaymentPolicy paymentPolicy);

	void shiftAccountingMonth(AggregateId contractId);

	void shiftAccountingMonth(YearMonth month);

    ContractData getContractData(AggregateId contractId);
}