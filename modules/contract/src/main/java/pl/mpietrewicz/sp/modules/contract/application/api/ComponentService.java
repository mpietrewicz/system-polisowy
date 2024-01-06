
package pl.mpietrewicz.sp.modules.contract.application.api;


import pl.mpietrewicz.sp.ddd.sharedkernel.Amount;
import pl.mpietrewicz.sp.modules.contract.domain.component.Component;
import pl.mpietrewicz.sp.ddd.canonicalmodel.publishedlanguage.AggregateId;

import java.time.LocalDate;

public interface ComponentService {

	Component addComponent(AggregateId contractId, String number, LocalDate registerDate, Amount premium);

	void terminate(String number, LocalDate endDate);

}