package pl.mpietrewicz.sp.modules.contract.domain.premium;

import lombok.RequiredArgsConstructor;
import pl.mpietrewicz.sp.ddd.annotations.domain.DomainService;
import pl.mpietrewicz.sp.ddd.canonicalmodel.publishedlanguage.snapshot.ComponentData;
import pl.mpietrewicz.sp.modules.contract.domain.component.Component;

import java.math.BigDecimal;
import java.time.LocalDate;

@DomainService
@RequiredArgsConstructor
public class PremiumDomainService {

    public void change(Premium premium, Component component, LocalDate since, BigDecimal amount) {
        premium.changePremium(amount, since);
        ComponentData componentData = component.generateSnapshot();
    }

}