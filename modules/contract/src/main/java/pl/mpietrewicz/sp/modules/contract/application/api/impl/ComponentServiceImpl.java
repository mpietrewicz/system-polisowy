package pl.mpietrewicz.sp.modules.contract.application.api.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;
import pl.mpietrewicz.sp.ddd.annotations.application.ApplicationService;
import pl.mpietrewicz.sp.ddd.canonicalmodel.events.ComponentCreatedEvent;
import pl.mpietrewicz.sp.ddd.canonicalmodel.events.ComponentTerminatedEvent;
import pl.mpietrewicz.sp.ddd.canonicalmodel.publishedlanguage.AggregateId;
import pl.mpietrewicz.sp.ddd.canonicalmodel.publishedlanguage.snapshot.ComponentData;
import pl.mpietrewicz.sp.ddd.canonicalmodel.publishedlanguage.snapshot.ContractData;
import pl.mpietrewicz.sp.ddd.support.domain.DomainEventPublisher;
import pl.mpietrewicz.sp.modules.contract.application.api.ComponentService;
import pl.mpietrewicz.sp.modules.contract.domain.component.Component;
import pl.mpietrewicz.sp.modules.contract.domain.component.ComponentDomainService;
import pl.mpietrewicz.sp.modules.contract.domain.component.ComponentFactory;
import pl.mpietrewicz.sp.modules.contract.domain.contract.Contract;
import pl.mpietrewicz.sp.modules.contract.domain.premium.Premium;
import pl.mpietrewicz.sp.modules.contract.domain.premium.PremiumFactory;
import pl.mpietrewicz.sp.modules.contract.domain.termination.Termination;
import pl.mpietrewicz.sp.modules.contract.domain.termination.TerminationFactory;
import pl.mpietrewicz.sp.modules.contract.infrastructure.repo.ComponentRepository;
import pl.mpietrewicz.sp.modules.contract.infrastructure.repo.ContractRepository;
import pl.mpietrewicz.sp.modules.contract.infrastructure.repo.PremiumRepository;
import pl.mpietrewicz.sp.modules.contract.infrastructure.repo.TerminationRepository;

import java.math.BigDecimal;
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
    private final PremiumFactory premiumFactory;

    @Override
    public Component addComponent(AggregateId contractId, LocalDate registerDate, BigDecimal premiumAmount) {
        Contract contract = contractRepository.load(contractId);

        ContractData contractData = contract.generateSnapshot();
        Component component = componentFactory.createAdditionalComponent(contractData, registerDate);
        componentRepository.save(component);

        ComponentData componentData = component.generateSnapshot();
        Premium premium = premiumFactory.create(componentData, registerDate, premiumAmount);
        premiumRepository.save(premium);

        ComponentCreatedEvent event = new ComponentCreatedEvent(componentData, premiumAmount);
        domainEventPublisher.publish(event);

        return component;
    }

    @Override
    public void terminate(AggregateId componentId, LocalDate terminatedDate) {
        Component component = componentRepository.load(componentId);
        componentDomainService.terminateComponent(component, terminatedDate); // todo: jak zakończyć wszystki składniki gdy zakończenie dotyczy składnika podstawowego?

        ComponentData componentData = component.generateSnapshot();
        Termination termination = terminationFactory.create(componentData, terminatedDate);
        terminationRepository.save(termination);

        ComponentTerminatedEvent event = new ComponentTerminatedEvent(component.generateSnapshot(), terminatedDate);
        domainEventPublisher.publish(event);
    }

}