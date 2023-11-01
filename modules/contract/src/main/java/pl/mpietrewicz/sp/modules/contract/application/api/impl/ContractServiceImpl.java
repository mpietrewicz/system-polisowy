package pl.mpietrewicz.sp.modules.contract.application.api.impl;

import lombok.RequiredArgsConstructor;
import pl.mpietrewicz.sp.ddd.annotations.application.ApplicationService;
import pl.mpietrewicz.sp.ddd.canonicalmodel.events.ContractCreatedEvent;
import pl.mpietrewicz.sp.ddd.canonicalmodel.publishedlanguage.AggregateId;
import pl.mpietrewicz.sp.ddd.canonicalmodel.publishedlanguage.Frequency;
import pl.mpietrewicz.sp.ddd.canonicalmodel.publishedlanguage.PaymentPolicy;
import pl.mpietrewicz.sp.ddd.canonicalmodel.publishedlanguage.snapshot.ComponentData;
import pl.mpietrewicz.sp.ddd.canonicalmodel.publishedlanguage.snapshot.ContractData;
import pl.mpietrewicz.sp.ddd.support.domain.DomainEventPublisher;
import pl.mpietrewicz.sp.modules.contract.application.api.ContractService;
import pl.mpietrewicz.sp.modules.contract.domain.component.Component;
import pl.mpietrewicz.sp.modules.contract.domain.component.ComponentFactory;
import pl.mpietrewicz.sp.modules.contract.domain.component.ComponentRepository;
import pl.mpietrewicz.sp.modules.contract.domain.contract.Contract;
import pl.mpietrewicz.sp.modules.contract.domain.contract.ContractFactory;
import pl.mpietrewicz.sp.modules.contract.domain.contract.ContractRepository;
import pl.mpietrewicz.sp.modules.contract.domain.premium.Premium;
import pl.mpietrewicz.sp.modules.contract.domain.premium.PremiumFactory;
import pl.mpietrewicz.sp.modules.contract.domain.premium.PremiumRepository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;

@ApplicationService
@RequiredArgsConstructor
public class ContractServiceImpl implements ContractService {

    private final ContractRepository contractRepository;
    private final ComponentRepository componentRepository;
    private final PremiumRepository premiumRepository;
    private final ComponentFactory componentFactory;
    private final PremiumFactory premiumFactory;
    private final ContractFactory contractFactory;
    private final DomainEventPublisher domainEventPublisher;


    @Override
    public Contract createContract(LocalDate registerDate, BigDecimal premiumAmount, Frequency frequency,
                                   PaymentPolicy paymentPolicy) {
        Contract contract = contractFactory.createContract(registerDate, frequency, paymentPolicy);
        contractRepository.save(contract);

        ContractData contractData = contract.generateSnapshot();
        Component component = componentFactory.createBasicComponent(contractData);
        componentRepository.save(component);

        ComponentData componentData = component.generateSnapshot();
        Premium premium = premiumFactory.create(componentData, registerDate, premiumAmount);
        premiumRepository.save(premium);

        ContractCreatedEvent event = new ContractCreatedEvent(contractData, premiumAmount, frequency);
        domainEventPublisher.publish(event);

        return contract;
    }

    @Override
    public void shiftAccountingMonth(AggregateId contractId) {
        Contract contract = contractRepository.load(contractId);
        contract.shiftAccountingMonth();
    }

    @Override
    public void shiftAccountingMonth(YearMonth month) {
        contractRepository.findAll().stream()
                .spliterator()
                .forEachRemaining(contract -> contract.shiftAccountingMonth(month));
    }

    @Override
    public ContractData getContractData(AggregateId contractId) {
        Contract contract = contractRepository.load(contractId);
        return contract.generateSnapshot();
    }

}