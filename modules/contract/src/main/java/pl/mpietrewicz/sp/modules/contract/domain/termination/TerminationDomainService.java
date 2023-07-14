package pl.mpietrewicz.sp.modules.contract.domain.termination;

import lombok.RequiredArgsConstructor;
import pl.mpietrewicz.sp.ddd.annotations.domain.DomainService;
import pl.mpietrewicz.sp.modules.contract.domain.component.Component;
import pl.mpietrewicz.sp.ddd.canonicalmodel.publishedlanguage.snapshot.ComponentData;

import java.time.LocalDate;

@DomainService
@RequiredArgsConstructor
public class TerminationDomainService {

    private final TerminationFactory terminationFactory;

    public Termination createTermination(Component component, LocalDate terminatedDate) {
        ComponentData componentData = component.generateSnapshot();
        return terminationFactory.create(componentData, terminatedDate);
    }

}