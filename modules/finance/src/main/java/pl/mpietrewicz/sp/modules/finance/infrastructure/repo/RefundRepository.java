
package pl.mpietrewicz.sp.modules.finance.infrastructure.repo;

import pl.mpietrewicz.sp.ddd.annotations.domain.DomainRepository;
import pl.mpietrewicz.sp.ddd.canonicalmodel.publishedlanguage.AggregateId;
import pl.mpietrewicz.sp.modules.finance.domain.refund.Refund;

import java.util.List;

@DomainRepository
public interface RefundRepository {

	Refund load(AggregateId paymentId);
	
	void save(Refund payment);

	List<Refund> findBy(AggregateId contractId);
}