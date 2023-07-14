package pl.mpietrewicz.sp.modules.contract.domain.contract.policy.impl;

import pl.mpietrewicz.sp.ddd.annotations.domain.DomainPolicyImpl;
import pl.mpietrewicz.sp.DateUtils;
import pl.mpietrewicz.sp.modules.contract.domain.contract.policy.ContractStartDatePolicy;

import java.time.LocalDate;

@DomainPolicyImpl
public class FirstDayOfMonthCSDPolicy implements ContractStartDatePolicy {

    @Override
    public LocalDate specifyStartDate(LocalDate registerDate) {
        return DateUtils.pierwszyDzienMiesiaca(registerDate);
    }

}