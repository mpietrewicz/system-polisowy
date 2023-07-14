package pl.mpietrewicz.sp.modules.contract.domain.contract.policy;

import pl.mpietrewicz.sp.ddd.annotations.domain.DomainPolicy;

import java.time.LocalDate;

@DomainPolicy
public interface ContractStartDatePolicy {

    LocalDate specifyStartDate(LocalDate registerDate);

}