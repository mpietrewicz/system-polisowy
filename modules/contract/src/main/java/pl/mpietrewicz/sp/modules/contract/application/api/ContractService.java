
package pl.mpietrewicz.sp.modules.contract.application.api;


import pl.mpietrewicz.sp.ddd.canonicalmodel.publishedlanguage.AggregateId;
import pl.mpietrewicz.sp.ddd.canonicalmodel.publishedlanguage.Frequency;
import pl.mpietrewicz.sp.ddd.canonicalmodel.publishedlanguage.PaymentPolicyEnum;
import pl.mpietrewicz.sp.ddd.canonicalmodel.publishedlanguage.snapshot.ContractData;
import pl.mpietrewicz.sp.ddd.sharedkernel.Amount;
import pl.mpietrewicz.sp.modules.contract.domain.contract.Contract;

import java.time.LocalDate;
import java.time.YearMonth;

public interface ContractService {

	Contract createContract(LocalDate registerDate, Amount premiumAmount, Frequency frequency, PaymentPolicyEnum paymentPolicyEnum);

	void shiftAccountingMonth(AggregateId contractId);

	void shiftAccountingMonth(YearMonth month);

    ContractData getContractData(AggregateId contractId);
}