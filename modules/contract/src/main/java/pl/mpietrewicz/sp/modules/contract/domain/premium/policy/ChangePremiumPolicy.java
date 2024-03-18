package pl.mpietrewicz.sp.modules.contract.domain.premium.policy;

import pl.mpietrewicz.sp.ddd.annotations.domain.DomainPolicy;
import pl.mpietrewicz.sp.modules.contract.domain.premium.operation.Operation;

import java.time.LocalDate;
import java.util.List;

@DomainPolicy
public interface ChangePremiumPolicy {

    boolean isAvailable(List<Operation> operations, LocalDate date);

}