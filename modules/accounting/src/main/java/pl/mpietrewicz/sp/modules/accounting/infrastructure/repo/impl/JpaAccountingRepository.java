
package pl.mpietrewicz.sp.modules.accounting.infrastructure.repo.impl;

import pl.mpietrewicz.sp.ddd.annotations.domain.DomainRepositoryImpl;
import pl.mpietrewicz.sp.ddd.support.infrastructure.repo.GenericJpaRepository;
import pl.mpietrewicz.sp.modules.accounting.domain.subledger.Subledger;
import pl.mpietrewicz.sp.modules.accounting.infrastructure.repo.SubledgerRepository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@DomainRepositoryImpl
public class JpaAccountingRepository extends GenericJpaRepository<Subledger> implements SubledgerRepository {

    @PersistenceContext(unitName = "accounting")
    public EntityManager entityManager;

    @Override
    public EntityManager getEntityManager() {
        return entityManager;
    }

    @Override
    public Subledger find() {
        String query = "SELECT a FROM Accounting a";
        return entityManager.createQuery(query, Subledger.class)
                .getSingleResult();
    }

}