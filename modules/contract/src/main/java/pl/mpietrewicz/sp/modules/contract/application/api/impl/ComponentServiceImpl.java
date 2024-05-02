package pl.mpietrewicz.sp.modules.contract.application.api.impl;

import lombok.RequiredArgsConstructor;
import pl.mpietrewicz.sp.ddd.annotations.application.ApplicationService;
import pl.mpietrewicz.sp.ddd.canonicalmodel.publishedlanguage.AggregateId;
import pl.mpietrewicz.sp.ddd.sharedkernel.valueobject.PositiveAmount;
import pl.mpietrewicz.sp.modules.contract.application.api.ComponentService;
import pl.mpietrewicz.sp.modules.contract.domain.component.Component;
import pl.mpietrewicz.sp.modules.contract.domain.component.ComponentDomainService;
import pl.mpietrewicz.sp.modules.contract.domain.contract.Contract;
import pl.mpietrewicz.sp.modules.contract.domain.premium.Premium;
import pl.mpietrewicz.sp.modules.contract.infrastructure.repo.ComponentRepository;
import pl.mpietrewicz.sp.modules.contract.infrastructure.repo.ContractRepository;
import pl.mpietrewicz.sp.modules.contract.infrastructure.repo.PremiumRepository;

import java.time.LocalDate;

@ApplicationService(boundedContext = "contract", transactionManager = "contractTransactionManager")
@RequiredArgsConstructor
public class ComponentServiceImpl implements ComponentService {

    private final ComponentDomainService componentDomainService;
    private final ContractRepository contractRepository;
    private final ComponentRepository componentRepository;
    private final PremiumRepository premiumRepository;

    @Override
    public void addComponent(AggregateId contractId, String name, LocalDate start, PositiveAmount premiumAmount) {
        Contract contract = contractRepository.load(contractId);
        Premium premium = premiumRepository.findBy(contractId);

        Component component = componentDomainService.addComponent(contract, premium, name, start, premiumAmount);
        componentRepository.save(component);
    }

    @Override
    public void terminate(AggregateId componentId, LocalDate terminatedDate) {
        Component component = componentRepository.load(componentId);
        Premium premium = premiumRepository.findBy(component.getContractId());

        componentDomainService.terminateComponent(component, premium, terminatedDate);
    }

}