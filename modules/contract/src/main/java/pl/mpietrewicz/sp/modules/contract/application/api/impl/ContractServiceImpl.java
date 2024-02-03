package pl.mpietrewicz.sp.modules.contract.application.api.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;
import pl.mpietrewicz.sp.ddd.annotations.application.ApplicationService;
import pl.mpietrewicz.sp.ddd.canonicalmodel.events.ContractCreatedEvent;
import pl.mpietrewicz.sp.ddd.canonicalmodel.publishedlanguage.AggregateId;
import pl.mpietrewicz.sp.ddd.canonicalmodel.publishedlanguage.Frequency;
import pl.mpietrewicz.sp.ddd.canonicalmodel.publishedlanguage.PaymentPolicyEnum;
import pl.mpietrewicz.sp.ddd.canonicalmodel.publishedlanguage.snapshot.ComponentData;
import pl.mpietrewicz.sp.ddd.canonicalmodel.publishedlanguage.snapshot.ContractData;
import pl.mpietrewicz.sp.ddd.canonicalmodel.publishedlanguage.snapshot.premium.PremiumSnapshot;
import pl.mpietrewicz.sp.ddd.sharedkernel.Amount;
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
import java.time.YearMonth;

@ApplicationService(transactional = @Transactional(
        transactionManager = "accountingTransactionManager"))
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
    public Contract createContract(String number, LocalDate registerDate, Amount premiumAmount, Frequency frequency,
                                   PaymentPolicyEnum paymentPolicyEnum) {
        Contract contract = contractFactory.createContract(registerDate, frequency, paymentPolicyEnum);
        contractRepository.save(contract);

        ContractData contractData = contract.generateSnapshot();
        Component basicComponent = componentFactory.createBasicComponent(contractData, number);
        componentRepository.save(basicComponent);

        ComponentData componentData = basicComponent.generateSnapshot();
        Premium premium = premiumFactory.create(contractData, componentData, premiumAmount);
        premiumRepository.save(premium);

        PremiumSnapshot premiumSnapshot = premium.generateSnapshot(LocalDateTime.now());
        ContractCreatedEvent event = new ContractCreatedEvent(contractData, premiumSnapshot);
        eventPublisher.publish(event, "ContractServiceImpl");

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