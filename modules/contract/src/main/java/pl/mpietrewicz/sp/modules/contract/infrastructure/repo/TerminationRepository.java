
package pl.mpietrewicz.sp.modules.contract.infrastructure.repo;

import pl.mpietrewicz.sp.ddd.annotations.domain.DomainRepository;
import pl.mpietrewicz.sp.ddd.canonicalmodel.publishedlanguage.AggregateId;
import pl.mpietrewicz.sp.modules.contract.domain.termination.Termination;

@DomainRepository
public interface TerminationRepository {

	Termination load(AggregateId id);

	void save(Termination termination);

}