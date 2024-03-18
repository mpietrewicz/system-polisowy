
package pl.mpietrewicz.sp.modules.contract.application.api;


import pl.mpietrewicz.sp.ddd.canonicalmodel.publishedlanguage.AggregateId;
import pl.mpietrewicz.sp.ddd.sharedkernel.valueobject.Amount;

import java.time.LocalDate;

public interface ComponentService {

	void addComponent(AggregateId contractId, String name, LocalDate registerDate, Amount premium);

	void terminate(AggregateId componentId, LocalDate endDate);

}