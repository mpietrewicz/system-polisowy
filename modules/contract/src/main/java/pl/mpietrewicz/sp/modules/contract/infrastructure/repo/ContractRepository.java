
package pl.mpietrewicz.sp.modules.contract.infrastructure.repo;

import pl.mpietrewicz.sp.ddd.annotations.domain.DomainRepository;
import pl.mpietrewicz.sp.ddd.canonicalmodel.publishedlanguage.AggregateId;
import pl.mpietrewicz.sp.modules.contract.domain.contract.Contract;

import java.util.List;
import java.util.Optional;

@DomainRepository
public interface ContractRepository {

	Contract load(AggregateId id);

	void save(Contract contract);

	List<Contract> find();

	Optional<Contract> findBy(AggregateId contractId);

	Optional<Contract> findBy(String name);

}