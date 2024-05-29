
package pl.mpietrewicz.sp.modules.contract.application.api;


import pl.mpietrewicz.sp.ddd.canonicalmodel.publishedlanguage.AggregateId;
import pl.mpietrewicz.sp.ddd.canonicalmodel.publishedlanguage.snapshot.premium.PremiumSnapshot;
import pl.mpietrewicz.sp.ddd.sharedkernel.valueobject.PositiveAmount;

import java.time.LocalDate;
import java.time.LocalDateTime;

public interface PremiumService {

	void change(AggregateId componentId, LocalDate date, PositiveAmount premium);

	void cancel(AggregateId componentId);

	PremiumSnapshot getPremiumSnapshot(AggregateId contractId, LocalDateTime timestamp);

}