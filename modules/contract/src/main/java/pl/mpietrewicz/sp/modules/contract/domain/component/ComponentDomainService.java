package pl.mpietrewicz.sp.modules.contract.domain.component;

import lombok.RequiredArgsConstructor;
import pl.mpietrewicz.sp.ddd.annotations.domain.DomainService;
import pl.mpietrewicz.sp.ddd.canonicalmodel.publishedlanguage.snapshot.ContractData;
import pl.mpietrewicz.sp.ddd.sharedkernel.valueobject.Amount;
import pl.mpietrewicz.sp.modules.contract.domain.contract.Contract;
import pl.mpietrewicz.sp.modules.contract.domain.premium.Premium;

import java.time.LocalDate;

import static pl.mpietrewicz.sp.modules.contract.domain.premium.ChangePremiumPolicyEnum.EVERYTIME;

@DomainService
@RequiredArgsConstructor
public class ComponentDomainService {

    private final ComponentFactory componentFactory;

    public Component addComponent(Contract contract, Premium premium, String name,LocalDate registration,
                                  Amount premiumAmount) {
        ContractData contractData = contract.generateSnapshot();
        Component component = componentFactory.createAdditionalComponent(contractData, name, registration);
        LocalDate startDate = component.getStart();

        premium.add(component.getAggregateId(), startDate, premiumAmount, EVERYTIME);

        return component;
    }

    public void terminateComponent(Component component, Premium premium, LocalDate terminatedDate) {
        component.terminate(terminatedDate);

        premium.delete(component.getAggregateId(), terminatedDate);
    }

}