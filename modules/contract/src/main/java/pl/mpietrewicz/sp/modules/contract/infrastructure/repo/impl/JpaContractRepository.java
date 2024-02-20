
package pl.mpietrewicz.sp.modules.contract.infrastructure.repo.impl;

import pl.mpietrewicz.sp.ddd.annotations.domain.DomainRepositoryImpl;
import pl.mpietrewicz.sp.ddd.canonicalmodel.publishedlanguage.AggregateId;
import pl.mpietrewicz.sp.ddd.support.infrastructure.repo.GenericJpaRepository;
import pl.mpietrewicz.sp.modules.contract.domain.contract.Contract;
import pl.mpietrewicz.sp.modules.contract.infrastructure.repo.ContractRepository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.ArrayList;
import java.util.List;

@DomainRepositoryImpl
public class JpaContractRepository extends GenericJpaRepository<Contract> implements ContractRepository {

    @PersistenceContext(unitName = "contract")
    public EntityManager entityManager;

    @Override
    public EntityManager getEntityManager() {
        return entityManager;
    }

    @Override
    public List<Contract> findAll() {
        String query = "SELECT c FROM Contract c";
        return new ArrayList<>(entityManager.createQuery(query, Contract.class)
                .getResultList());
    }

}