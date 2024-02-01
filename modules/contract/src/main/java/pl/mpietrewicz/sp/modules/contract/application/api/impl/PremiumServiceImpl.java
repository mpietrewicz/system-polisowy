package pl.mpietrewicz.sp.modules.contract.application.api.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;
import pl.mpietrewicz.sp.ddd.annotations.application.ApplicationService;
import pl.mpietrewicz.sp.ddd.canonicalmodel.publishedlanguage.AggregateId;
import pl.mpietrewicz.sp.ddd.canonicalmodel.publishedlanguage.snapshot.ComponentData;
import pl.mpietrewicz.sp.ddd.canonicalmodel.publishedlanguage.snapshot.premium.PremiumSnapshot;
import pl.mpietrewicz.sp.ddd.sharedkernel.Amount;
import pl.mpietrewicz.sp.modules.contract.application.api.PremiumService;
import pl.mpietrewicz.sp.modules.contract.domain.component.Component;
import pl.mpietrewicz.sp.modules.contract.domain.premium.Premium;
import pl.mpietrewicz.sp.modules.contract.domain.premium.PremiumDomainService;
import pl.mpietrewicz.sp.modules.contract.infrastructure.repo.ComponentRepository;
import pl.mpietrewicz.sp.modules.contract.infrastructure.repo.PremiumRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;

@ApplicationService(transactional = @Transactional(
        transactionManager = "contractTransactionManager"))
@RequiredArgsConstructor
public class PremiumServiceImpl implements PremiumService {

    private final PremiumRepository premiumRepository;
    private final ComponentRepository componentRepository;
    private final PremiumDomainService premiumDomainService;

    @Override
    public void change(AggregateId componentId, LocalDate since, Amount amount) {
        Premium premium = premiumRepository.findByComponentId(componentId);
        Component component = componentRepository.load(componentId);

        premiumDomainService.change(premium, component, since, amount);
    }

    @Override
    public void cancel(AggregateId componentId) {
        Premium premium = premiumRepository.findByComponentId(componentId);
        Component component = componentRepository.load(componentId);

        premiumDomainService.cancel(premium, component);
    }

    @Override
    public void cancel(String numerSkladnika) {
        Component component = componentRepository.findByNumber(numerSkladnika);

        ComponentData componentData = component.generateSnapshot();
        Premium premium = premiumRepository.findByComponentId(componentData.getAggregateId());

        premiumDomainService.cancel(premium, component);
    }


    @Override
    public PremiumSnapshot getPremiumSnapshot(AggregateId contractId, LocalDateTime timestamp) {
        Premium premium = premiumRepository.findByContractId(contractId);
        return premium.generateSnapshot(timestamp);
    }

}