
package pl.mpietrewicz.sp.modules.accounting.impl;

import pl.mpietrewicz.sp.modules.accounting.domain.accounting.Accounting;
import pl.mpietrewicz.sp.modules.accounting.domain.accounting.AccountingRepository;
import pl.mpietrewicz.sp.ddd.annotations.domain.DomainRepositoryImpl;
import pl.mpietrewicz.sp.ddd.support.infrastructure.repository.jpa.GenericJpaRepository;

@DomainRepositoryImpl
public class JpaAccountingRepository extends GenericJpaRepository<Accounting> implements AccountingRepository {

    @Override
    public Accounting find() {
        String query = "SELECT a FROM Accounting a";
        return entityManager.createQuery(query, Accounting.class)
                .getSingleResult();
    }

}