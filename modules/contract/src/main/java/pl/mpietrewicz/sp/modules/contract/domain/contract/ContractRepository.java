
package pl.mpietrewicz.sp.modules.contract.domain.contract;

import pl.mpietrewicz.sp.ddd.annotations.domain.DomainRepository;
import pl.mpietrewicz.sp.ddd.canonicalmodel.publishedlanguage.AggregateId;

import java.util.List;

@DomainRepository
public interface ContractRepository {

	Contract load(AggregateId id);

	void save(Contract contract);

	List<Contract> findAll();

	Contract findByComponentId(AggregateId componentId);

}