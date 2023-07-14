
package pl.mpietrewicz.sp.modules.contract.application.api;


import pl.mpietrewicz.sp.ddd.canonicalmodel.publishedlanguage.AggregateId;

import java.math.BigDecimal;
import java.time.LocalDate;

public interface PremiumService {

	void change(AggregateId componentId, LocalDate date, BigDecimal amount);

}