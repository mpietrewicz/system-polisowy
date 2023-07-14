
package pl.mpietrewicz.sp.modules.contract.domain.termination;

import pl.mpietrewicz.sp.ddd.annotations.domain.DomainRepository;
import pl.mpietrewicz.sp.ddd.canonicalmodel.publishedlanguage.AggregateId;

@DomainRepository
public interface TerminationRepository {

	Termination load(AggregateId id);

	void save(Termination termination);

}