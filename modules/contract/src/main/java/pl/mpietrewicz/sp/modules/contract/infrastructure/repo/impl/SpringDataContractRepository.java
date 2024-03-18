package pl.mpietrewicz.sp.modules.contract.infrastructure.repo.impl;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.mpietrewicz.sp.ddd.annotations.domain.DomainRepositoryImpl;
import pl.mpietrewicz.sp.ddd.canonicalmodel.publishedlanguage.AggregateId;
import pl.mpietrewicz.sp.modules.contract.domain.contract.Contract;

import java.util.List;
import java.util.Optional;

@DomainRepositoryImpl
public interface SpringDataContractRepository extends JpaRepository<Contract, String> {

    List<Contract> findAll();

    Optional<Contract> findByAggregateId(AggregateId aggregateId);

}