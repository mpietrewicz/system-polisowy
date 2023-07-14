package pl.mpietrewicz.sp.modules.contract.domain.component;

import lombok.RequiredArgsConstructor;
import pl.mpietrewicz.sp.ddd.annotations.domain.DomainService;

import java.time.LocalDate;

@DomainService
@RequiredArgsConstructor
public class ComponentDomainService {

    private final ComponentFactory componentFactory;

    public void terminateComponent(Component component, LocalDate terminatedDate) {
        component.terminate(terminatedDate);
    }
}