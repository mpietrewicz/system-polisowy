
package pl.mpietrewicz.sp.modules.contract.infrastructure.repo.impl;

import pl.mpietrewicz.sp.ddd.annotations.domain.DomainRepositoryImpl;
import pl.mpietrewicz.sp.ddd.canonicalmodel.publishedlanguage.AggregateId;
import pl.mpietrewicz.sp.modules.contract.ddd.support.infrastructure.repository.jpa.GenericJpaRepository;
import pl.mpietrewicz.sp.modules.contract.domain.contract.Contract;
import pl.mpietrewicz.sp.modules.contract.infrastructure.repo.ContractRepository;

import java.util.ArrayList;
import java.util.List;

@DomainRepositoryImpl
public class JpaContractRepository extends GenericJpaRepository<Contract> implements ContractRepository {

    @Override
    public List<Contract> findAll() {
        String query = "SELECT c FROM Contract c";
        return new ArrayList<>(entityManager.createQuery(query, Contract.class)
                .getResultList());
    }

    @Override
    public Contract findByComponentId(AggregateId componentId) {
        String query = "SELECT c FROM Contract c, Component cp " +
                "WHERE cp.aggregateId = :componentId " +
                "AND cp.contractData.aggregateId = c.aggregateId";
        return entityManager.createQuery(query, Contract.class)
                .setParameter("componentId", componentId)
                .getSingleResult();
    }

}