package pl.mpietrewicz.sp.modules.contract.infrastructure.repo.impl;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.mpietrewicz.sp.ddd.annotations.domain.DomainRepositoryImpl;
import pl.mpietrewicz.sp.ddd.canonicalmodel.publishedlanguage.AggregateId;
import pl.mpietrewicz.sp.modules.contract.domain.component.Component;

import java.util.List;
import java.util.Optional;

@DomainRepositoryImpl
public interface SpringDataComponentRepository extends JpaRepository<Component, String> {

    List<Component> findByContractId(AggregateId contractId);

    Optional<Component> findByContractIdAndAggregateId(AggregateId contractId, AggregateId aggregateId);

    Optional<Component> findByContractIdAndName(AggregateId contractId, String name);

}