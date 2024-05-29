package pl.mpietrewicz.sp.modules.accounting.infrastructure.repo.impl;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.mpietrewicz.sp.ddd.annotations.domain.DomainRepositoryImpl;
import pl.mpietrewicz.sp.ddd.canonicalmodel.publishedlanguage.AggregateId;
import pl.mpietrewicz.sp.modules.accounting.domain.allocation.Allocation;

import java.util.Optional;

@DomainRepositoryImpl
public interface SpringDataAllocationRepository extends JpaRepository<Allocation, String> {

    Optional<Allocation> findByContractId(AggregateId contractId);

}