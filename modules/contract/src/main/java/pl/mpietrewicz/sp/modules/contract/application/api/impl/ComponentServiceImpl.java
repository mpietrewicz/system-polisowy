package pl.mpietrewicz.sp.modules.contract.application.api.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;
import pl.mpietrewicz.sp.ddd.annotations.application.ApplicationService;
import pl.mpietrewicz.sp.ddd.canonicalmodel.publishedlanguage.AggregateId;
import pl.mpietrewicz.sp.ddd.canonicalmodel.publishedlanguage.snapshot.ComponentData;
import pl.mpietrewicz.sp.ddd.canonicalmodel.publishedlanguage.snapshot.ContractData;
import pl.mpietrewicz.sp.ddd.sharedkernel.Amount;
import pl.mpietrewicz.sp.ddd.support.domain.DomainEventPublisher;
import pl.mpietrewicz.sp.modules.contract.application.api.ComponentService;
import pl.mpietrewicz.sp.modules.contract.domain.component.Component;
import pl.mpietrewicz.sp.modules.contract.domain.component.ComponentDomainService;
import pl.mpietrewicz.sp.modules.contract.domain.component.ComponentFactory;
import pl.mpietrewicz.sp.modules.contract.domain.contract.Contract;
import pl.mpietrewicz.sp.modules.contract.domain.premium.Premium;
import pl.mpietrewicz.sp.modules.contract.domain.termination.Termination;
import pl.mpietrewicz.sp.modules.contract.domain.termination.TerminationFactory;
import pl.mpietrewicz.sp.modules.contract.infrastructure.repo.ComponentRepository;
import pl.mpietrewicz.sp.modules.contract.infrastructure.repo.ContractRepository;
import pl.mpietrewicz.sp.modules.contract.infrastructure.repo.PremiumRepository;
import pl.mpietrewicz.sp.modules.contract.infrastructure.repo.TerminationRepository;

import java.time.LocalDate;

@ApplicationService(transactional = @Transactional(
        transactionManager = "contractTransactionManager"))
@RequiredArgsConstructor
public class ComponentServiceImpl implements ComponentService {

    private final ComponentDomainService componentDomainService;
    private final ContractRepository contractRepository;
    private final ComponentRepository componentRepository;
    private final DomainEventPublisher domainEventPublisher;
    private final TerminationRepository terminationRepository;
    private final PremiumRepository premiumRepository;
    private final ComponentFactory componentFactory;
    private final TerminationFactory terminationFactory;

    @Override
    public Component addComponent(AggregateId contractId, String number, LocalDate registerDate, Amount premiumAmount) {
        Contract contract = contractRepository.load(contractId);

        ContractData contractData = contract.generateSnapshot();
        Component component = componentFactory.createAdditionalComponent(contractData, number, registerDate);
        componentRepository.save(component);

        Premium premium = premiumRepository.findByContractId(contractId);
        ComponentData componentData = component.generateSnapshot();
        premium.add(componentData, componentData.getStartDate(), premiumAmount);

        return component;
    }

    @Override
    public void terminate(String number, LocalDate terminatedDate) {
        Component component = componentRepository.findByNumber(number);

        ComponentData componentData = component.generateSnapshot();
        Termination termination = terminationFactory.create(componentData, terminatedDate);
        terminationRepository.save(termination);

        Premium premium = premiumRepository.findByContractId(component.getContractData().getAggregateId());// todo: brzydkie wyciągniecie contractId
        premium.delete(componentData, terminatedDate);

//        ComponentTerminatedEvent event = new ComponentTerminatedEvent(component.generateSnapshot(), terminatedDate);
//        domainEventPublisher.publish(event);
    }

}