package pl.mpietrewicz.sp.modules.contract.domain.contract.policy.impl;

import pl.mpietrewicz.sp.ddd.annotations.domain.DomainPolicyImpl;
import pl.mpietrewicz.sp.modules.contract.domain.contract.policy.ContractStartPolicy;

import java.time.LocalDate;

@DomainPolicyImpl
public class DailyStartPolicy implements ContractStartPolicy {

    @Override
    public LocalDate getStartDate(LocalDate registerDate) {
        return registerDate;
    }

}