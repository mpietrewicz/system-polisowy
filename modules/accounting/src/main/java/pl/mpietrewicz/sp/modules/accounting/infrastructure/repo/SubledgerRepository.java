package pl.mpietrewicz.sp.modules.accounting.infrastructure.repo;

import pl.mpietrewicz.sp.ddd.annotations.domain.DomainRepository;
import pl.mpietrewicz.sp.ddd.canonicalmodel.publishedlanguage.AggregateId;
import pl.mpietrewicz.sp.modules.accounting.domain.subledger.Subledger;

@DomainRepository
public interface SubledgerRepository {

	Subledger load(AggregateId id);

	void save(Subledger accounting);

	Subledger find();
}