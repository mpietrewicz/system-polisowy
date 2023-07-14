package pl.mpietrewicz.sp.modules.accounting.domain.accounting;

import pl.mpietrewicz.sp.ddd.annotations.domain.DomainRepository;
import pl.mpietrewicz.sp.ddd.canonicalmodel.publishedlanguage.AggregateId;

@DomainRepository
public interface AccountingRepository {

	Accounting load(AggregateId id);

	void save(Accounting accounting);

	Accounting find();
}