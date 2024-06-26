
package pl.mpietrewicz.sp.modules.contract.application.api;


import pl.mpietrewicz.sp.ddd.canonicalmodel.publishedlanguage.AggregateId;
import pl.mpietrewicz.sp.ddd.canonicalmodel.publishedlanguage.Frequency;
import pl.mpietrewicz.sp.ddd.canonicalmodel.publishedlanguage.snapshot.ContractData;
import pl.mpietrewicz.sp.ddd.sharedkernel.valueobject.PositiveAmount;
import pl.mpietrewicz.sp.modules.contract.domain.contract.Contract;

import java.time.LocalDate;

public interface ContractService {

	Contract createContract(String department, String name, LocalDate start, PositiveAmount premiumPositiveAmount, Frequency frequency);

    ContractData getContractData(AggregateId contractId);

	void endContract(AggregateId contractId, LocalDate date);

	void cancelEndContract(AggregateId contractId);

}