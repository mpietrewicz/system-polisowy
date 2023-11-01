package pl.mpietrewicz.sp.modules.accounting.domain.subledger;

import pl.mpietrewicz.sp.ddd.annotations.domain.DomainRepository;
import pl.mpietrewicz.sp.ddd.canonicalmodel.publishedlanguage.AggregateId;

@DomainRepository
public interface SubledgerRepository {

	Subledger load(AggregateId id);

	void save(Subledger accounting);

	Subledger find();
}