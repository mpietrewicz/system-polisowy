
package pl.mpietrewicz.sp.modules.contract.infrastructure.repo.impl;

import lombok.RequiredArgsConstructor;
import pl.mpietrewicz.sp.ddd.annotations.domain.DomainRepositoryImpl;
import pl.mpietrewicz.sp.ddd.canonicalmodel.publishedlanguage.AggregateId;
import pl.mpietrewicz.sp.ddd.support.infrastructure.repo.GenericJpaRepository;
import pl.mpietrewicz.sp.modules.contract.domain.contract.Contract;
import pl.mpietrewicz.sp.modules.contract.infrastructure.repo.ContractRepository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;
import java.util.Optional;

@DomainRepositoryImpl
@RequiredArgsConstructor
public class JpaContractRepository extends GenericJpaRepository<Contract> implements ContractRepository {

    private final SpringDataContractRepository springDataContractRepository;

    @PersistenceContext(unitName = "contract")
    public EntityManager entityManager;

    @Override
    public EntityManager getEntityManager() {
        return entityManager;
    }

    @Override
    public List<Contract> find() {
        return springDataContractRepository.findAll();
    }

    @Override
    public Optional<Contract> findBy(AggregateId contractId) {
        return springDataContractRepository.findByAggregateId(contractId);
    }

}