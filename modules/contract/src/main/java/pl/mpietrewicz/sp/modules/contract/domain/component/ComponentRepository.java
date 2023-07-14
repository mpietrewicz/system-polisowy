package pl.mpietrewicz.sp.modules.contract.domain.component;

import pl.mpietrewicz.sp.ddd.annotations.domain.DomainRepository;
import pl.mpietrewicz.sp.ddd.canonicalmodel.publishedlanguage.AggregateId;

import java.util.List;

@DomainRepository
public interface ComponentRepository {

	Component load(AggregateId id);

	void save(Component component);

	List<Component> findByContractId(AggregateId contractId);

}