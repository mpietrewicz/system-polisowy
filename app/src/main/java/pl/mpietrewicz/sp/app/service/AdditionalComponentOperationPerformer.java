package pl.mpietrewicz.sp.app.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pl.mpietrewicz.sp.app.readmodel.model.Operation;
import pl.mpietrewicz.sp.app.readmodel.model.Type;
import pl.mpietrewicz.sp.ddd.canonicalmodel.publishedlanguage.AggregateId;
import pl.mpietrewicz.sp.ddd.sharedkernel.exception.NotPositiveAmountException;
import pl.mpietrewicz.sp.ddd.sharedkernel.valueobject.PositiveAmount;
import pl.mpietrewicz.sp.modules.contract.application.api.ComponentService;
import pl.mpietrewicz.sp.modules.contract.application.api.PremiumService;
import pl.mpietrewicz.sp.modules.contract.domain.component.Component;
import pl.mpietrewicz.sp.modules.contract.infrastructure.repo.ComponentRepository;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class AdditionalComponentOperationPerformer implements OperationPerformer {

    private final ComponentService componentService;

    private final PremiumService premiumService;

    private final ComponentRepository componentRepository;

    public void perform(Operation operation, AggregateId contractId) throws NotPositiveAmountException {
        String componentName = operation.getComponent();
        LocalDate date = operation.getDate();
        PositiveAmount positiveAmount = operation.getAmount() != null
                ? PositiveAmount.withValue(operation.getAmount())
                : null;

        Type type = operation.getType();
        switch (type) {
            case DSK:
            case PUM:
                componentService.addComponent(contractId, componentName, date, positiveAmount);
                break;
            case PSU:
                Component component = componentRepository.findBy(contractId, componentName).orElseThrow();
                premiumService.change(component.getAggregateId(), date, positiveAmount);
                break;
            case ZOU:
                component = componentRepository.findBy(contractId, componentName).orElseThrow();
                componentService.terminate(component.getAggregateId(), date);
                break;
            case USK:
                component = componentRepository.findBy(contractId, componentName).orElseThrow();
                premiumService.cancel(component.getAggregateId());
                break;
            default:
                throw new UnsupportedOperationException();
        }
    }

}