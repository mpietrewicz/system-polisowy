package pl.mpietrewicz.sp.modules.contract.domain.premium;

import lombok.RequiredArgsConstructor;
import pl.mpietrewicz.sp.ddd.annotations.domain.DomainService;
import pl.mpietrewicz.sp.ddd.canonicalmodel.publishedlanguage.snapshot.ComponentData;
import pl.mpietrewicz.sp.ddd.sharedkernel.Amount;
import pl.mpietrewicz.sp.modules.contract.domain.component.Component;

import java.time.LocalDate;

@DomainService
@RequiredArgsConstructor
public class PremiumDomainService {

    public void change(Premium premium, Component component, LocalDate since, Amount premiumAmount) {
        // todo: mogę zmienić składkę zle z różnymi warunkami:
        // 1. Tylko 1 lub 2 razy w roku (w zależności od definicji) dla składników dodatkowych (zawsze dla podstawy)
        // 2. Na określoną kwotę per składnik (definicję składnika)
        // todo: wyżej wynioeniona wygląda jak polityka -> gdzie ją zmieścić?
        // todo: ODP: W środku agregatu Premium

        ComponentData componentData = component.generateSnapshot();
        premium.change(componentData, since, premiumAmount);
    }

    public void cancel(Premium premium, Component component) {
        ComponentData componentData = component.generateSnapshot();
        premium.cancel(componentData);
    }
}