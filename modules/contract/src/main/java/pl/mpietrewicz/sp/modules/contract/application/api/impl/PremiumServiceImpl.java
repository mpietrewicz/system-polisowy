package pl.mpietrewicz.sp.modules.contract.application.api.impl;

import lombok.RequiredArgsConstructor;
import pl.mpietrewicz.sp.ddd.annotations.application.ApplicationService;
import pl.mpietrewicz.sp.ddd.canonicalmodel.publishedlanguage.AggregateId;
import pl.mpietrewicz.sp.ddd.canonicalmodel.publishedlanguage.snapshot.premium.PremiumSnapshot;
import pl.mpietrewicz.sp.ddd.sharedkernel.valueobject.Amount;
import pl.mpietrewicz.sp.modules.contract.application.api.PremiumService;
import pl.mpietrewicz.sp.modules.contract.domain.component.Component;
import pl.mpietrewicz.sp.modules.contract.domain.premium.Premium;
import pl.mpietrewicz.sp.modules.contract.infrastructure.repo.ComponentRepository;
import pl.mpietrewicz.sp.modules.contract.infrastructure.repo.PremiumRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;

@ApplicationService(boundedContext = "contract", transactionManager = "contractTransactionManager")
@RequiredArgsConstructor
public class PremiumServiceImpl implements PremiumService {

    private final PremiumRepository premiumRepository;
    private final ComponentRepository componentRepository;

    @Override
    public void change(AggregateId componentId, LocalDate since, Amount amount) {
        Premium premium = premiumRepository.findByComponentId(componentId);
        Component component = componentRepository.load(componentId);

        premium.change(component.getAggregateId(), since, amount);
    }

    @Override
    public void cancel(AggregateId componentId) {
        Premium premium = premiumRepository.findByComponentId(componentId);
        Component component = componentRepository.load(componentId);

        premium.cancel(component.getAggregateId());
    }

    @Override
    public PremiumSnapshot getPremiumSnapshot(AggregateId contractId, LocalDateTime timestamp) {
        Premium premium = premiumRepository.findBy(contractId);
        return premium.generateSnapshot(timestamp);
    }

}