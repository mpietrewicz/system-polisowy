
package pl.mpietrewicz.sp.modules.contract.application.api;


import pl.mpietrewicz.sp.ddd.canonicalmodel.publishedlanguage.AggregateId;
import pl.mpietrewicz.sp.ddd.sharedkernel.Amount;

import java.time.LocalDate;

public interface PremiumService {

	void change(AggregateId componentId, LocalDate date, Amount amount);

}