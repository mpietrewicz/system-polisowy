package pl.mpietrewicz.sp.modules.contract.application.api.impl;

import lombok.RequiredArgsConstructor;
import pl.mpietrewicz.sp.ddd.annotations.application.ApplicationService;
import pl.mpietrewicz.sp.ddd.canonicalmodel.publishedlanguage.AggregateId;
import pl.mpietrewicz.sp.modules.contract.application.api.PremiumService;
import pl.mpietrewicz.sp.modules.contract.domain.component.Component;
import pl.mpietrewicz.sp.modules.contract.domain.component.ComponentRepository;
import pl.mpietrewicz.sp.modules.contract.domain.premium.Premium;
import pl.mpietrewicz.sp.modules.contract.domain.premium.PremiumDomainService;
import pl.mpietrewicz.sp.modules.contract.domain.premium.PremiumRepository;

import java.math.BigDecimal;
import java.time.LocalDate;

@ApplicationService
@RequiredArgsConstructor
public class PremiumServiceImpl implements PremiumService {

    private final PremiumRepository premiumRepository;
    private final ComponentRepository componentRepository;
    private final PremiumDomainService premiumDomainService;

    @Override
    public void change(AggregateId componentId, LocalDate since, BigDecimal amount) {
        Premium premium = premiumRepository.findByComponentId(componentId);
        Component component = componentRepository.load(componentId);
        // todo: nie wiem czy premium powinien być agregatem

        premiumDomainService.change(premium, component, since, amount);
    }

}