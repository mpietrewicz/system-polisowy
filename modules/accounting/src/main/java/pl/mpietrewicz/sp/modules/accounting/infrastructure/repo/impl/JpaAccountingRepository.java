
package pl.mpietrewicz.sp.modules.accounting.infrastructure.repo.impl;

import pl.mpietrewicz.sp.ddd.annotations.domain.DomainRepositoryImpl;
import pl.mpietrewicz.sp.modules.accounting.ddd.support.infrastructure.repository.jpa.GenericJpaRepository;
import pl.mpietrewicz.sp.modules.accounting.domain.subledger.Subledger;
import pl.mpietrewicz.sp.modules.accounting.infrastructure.repo.SubledgerRepository;

@DomainRepositoryImpl
public class JpaAccountingRepository extends GenericJpaRepository<Subledger> implements SubledgerRepository {

    @Override
    public Subledger find() {
        String query = "SELECT a FROM Accounting a";
        return entityManager.createQuery(query, Subledger.class)
                .getSingleResult();
    }

}