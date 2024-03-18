package pl.mpietrewicz.sp.modules.contract.application.api.impl;

import lombok.RequiredArgsConstructor;
import pl.mpietrewicz.sp.ddd.annotations.application.ApplicationService;
import pl.mpietrewicz.sp.ddd.canonicalmodel.events.CanceledContractEndEvent;
import pl.mpietrewicz.sp.ddd.canonicalmodel.events.ContractCreatedEvent;
import pl.mpietrewicz.sp.ddd.canonicalmodel.events.ContractEndedEvent;
import pl.mpietrewicz.sp.ddd.canonicalmodel.publishedlanguage.AggregateId;
import pl.mpietrewicz.sp.ddd.canonicalmodel.publishedlanguage.Frequency;
import pl.mpietrewicz.sp.ddd.canonicalmodel.publishedlanguage.snapshot.ComponentData;
import pl.mpietrewicz.sp.ddd.canonicalmodel.publishedlanguage.snapshot.ContractData;
import pl.mpietrewicz.sp.ddd.canonicalmodel.publishedlanguage.snapshot.premium.PremiumSnapshot;
import pl.mpietrewicz.sp.ddd.sharedkernel.valueobject.Amount;
import pl.mpietrewicz.sp.ddd.support.domain.DomainEventPublisher;
import pl.mpietrewicz.sp.modules.contract.application.api.ContractService;
import pl.mpietrewicz.sp.modules.contract.domain.component.Component;
import pl.mpietrewicz.sp.modules.contract.domain.component.ComponentFactory;
import pl.mpietrewicz.sp.modules.contract.domain.contract.Contract;
import pl.mpietrewicz.sp.modules.contract.domain.contract.ContractFactory;
import pl.mpietrewicz.sp.modules.contract.domain.premium.Premium;
import pl.mpietrewicz.sp.modules.contract.domain.premium.PremiumFactory;
import pl.mpietrewicz.sp.modules.contract.infrastructure.repo.ComponentRepository;
import pl.mpietrewicz.sp.modules.contract.infrastructure.repo.ContractRepository;
import pl.mpietrewicz.sp.modules.contract.infrastructure.repo.PremiumRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;

@ApplicationService(boundedContext = "contract", transactionManager = "contractTransactionManager")
@RequiredArgsConstructor
public class ContractServiceImpl implements ContractService {

    private final ContractRepository contractRepository;
    private final ComponentRepository componentRepository;
    private final PremiumRepository premiumRepository;
    private final ComponentFactory componentFactory;
    private final ContractFactory contractFactory;
    private final DomainEventPublisher eventPublisher;
    private final PremiumFactory premiumFactory;

    @Override
    public Contract createContract(String name, LocalDate start, Amount premiumAmount, Frequency frequency) {
        Contract contract = contractFactory.createContract(start, frequency);
        contractRepository.save(contract);

        ContractData contractData = contract.generateSnapshot();
        Component basicComponent = componentFactory.createBasicComponent(contractData, name);
        componentRepository.save(basicComponent);

        ComponentData componentData = basicComponent.generateSnapshot();
        Premium premium = premiumFactory.create(contractData, componentData, premiumAmount);
        premiumRepository.save(premium);

        PremiumSnapshot premiumSnapshot = premium.generateSnapshot(LocalDateTime.now());
        ContractCreatedEvent event = new ContractCreatedEvent(contractData, premiumSnapshot);
        eventPublisher.publish(event);

        return contract;
    }

    @Override
    public ContractData getContractData(AggregateId contractId) {
        Contract contract = contractRepository.load(contractId);
        return contract.generateSnapshot();
    }

    @Override
    public void endContract(AggregateId contractId, LocalDate date) {
        Contract contract = contractRepository.load(contractId);
        contract.end(date);

        ContractEndedEvent event = new ContractEndedEvent(contract.generateSnapshot(), date);
        eventPublisher.publish(event);
    }

    @Override
    public void cancelEndContract(AggregateId contractId) {
        Contract contract = contractRepository.load(contractId);
        contract.cancelEnd();

        CanceledContractEndEvent event = new CanceledContractEndEvent(contract.generateSnapshot());
        eventPublisher.publish(event);
    }

}