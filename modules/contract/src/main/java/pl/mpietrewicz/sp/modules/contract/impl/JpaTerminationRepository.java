
package pl.mpietrewicz.sp.modules.contract.impl;

import pl.mpietrewicz.sp.ddd.annotations.domain.DomainRepositoryImpl;
import pl.mpietrewicz.sp.ddd.support.infrastructure.repository.jpa.GenericJpaRepository;
import pl.mpietrewicz.sp.modules.contract.domain.termination.Termination;
import pl.mpietrewicz.sp.modules.contract.domain.termination.TerminationRepository;

@DomainRepositoryImpl
public class JpaTerminationRepository extends GenericJpaRepository<Termination> implements TerminationRepository {

}