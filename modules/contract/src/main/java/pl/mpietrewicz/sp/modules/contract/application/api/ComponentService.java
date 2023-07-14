
package pl.mpietrewicz.sp.modules.contract.application.api;


import pl.mpietrewicz.sp.modules.contract.domain.component.Component;
import pl.mpietrewicz.sp.ddd.canonicalmodel.publishedlanguage.AggregateId;

import java.math.BigDecimal;
import java.time.LocalDate;

public interface ComponentService {

	Component addComponent(AggregateId contractId, LocalDate registerDate, BigDecimal premium);

	void terminate(AggregateId componentId, LocalDate endDate);

}