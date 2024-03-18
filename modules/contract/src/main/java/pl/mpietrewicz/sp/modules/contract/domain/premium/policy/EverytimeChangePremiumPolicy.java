package pl.mpietrewicz.sp.modules.contract.domain.premium.policy;

import pl.mpietrewicz.sp.ddd.annotations.domain.DomainPolicyImpl;
import pl.mpietrewicz.sp.modules.contract.domain.premium.operation.Operation;

import java.time.LocalDate;
import java.util.List;

@DomainPolicyImpl
public class EverytimeChangePremiumPolicy implements ChangePremiumPolicy {

    @Override
    public boolean isAvailable(List<Operation> operations, LocalDate date) {
        return true;
    }

}