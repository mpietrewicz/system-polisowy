
package pl.mpietrewicz.sp.modules.accounting.impl;

import pl.mpietrewicz.sp.modules.accounting.domain.subledger.Subledger;
import pl.mpietrewicz.sp.modules.accounting.domain.subledger.SubledgerRepository;
import pl.mpietrewicz.sp.ddd.annotations.domain.DomainRepositoryImpl;
import pl.mpietrewicz.sp.ddd.support.infrastructure.repository.jpa.GenericJpaRepository;

@DomainRepositoryImpl
public class JpaAccountingRepository extends GenericJpaRepository<Subledger> implements SubledgerRepository {

    @Override
    public Subledger find() {
        String query = "SELECT a FROM Accounting a";
        return entityManager.createQuery(query, Subledger.class)
                .getSingleResult();
    }

}