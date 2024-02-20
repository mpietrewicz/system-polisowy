
package pl.mpietrewicz.sp.modules.contract.infrastructure.repo.impl;

import pl.mpietrewicz.sp.ddd.annotations.domain.DomainRepositoryImpl;
import pl.mpietrewicz.sp.ddd.support.infrastructure.repo.GenericJpaRepository;
import pl.mpietrewicz.sp.modules.contract.domain.termination.Termination;
import pl.mpietrewicz.sp.modules.contract.infrastructure.repo.TerminationRepository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@DomainRepositoryImpl
public class JpaTerminationRepository extends GenericJpaRepository<Termination> implements TerminationRepository {

    @PersistenceContext(unitName = "contract")
    public EntityManager entityManager;

    @Override
    public EntityManager getEntityManager() {
        return entityManager;
    }

}