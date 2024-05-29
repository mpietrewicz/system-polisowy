package pl.mpietrewicz.sp.modules.contract.infrastructure.repo;

import pl.mpietrewicz.sp.ddd.annotations.domain.DomainRepository;
import pl.mpietrewicz.sp.ddd.canonicalmodel.publishedlanguage.AggregateId;
import pl.mpietrewicz.sp.modules.contract.domain.component.Component;

import java.util.List;
import java.util.Optional;

@DomainRepository
public interface ComponentRepository {

	Component load(AggregateId id);

	void save(Component component);

	List<Component> findBy(AggregateId contractId);

	Optional<Component> findBy(AggregateId contractId, String name);

	Optional<Component> findBy(AggregateId contractId, AggregateId componentId);

}