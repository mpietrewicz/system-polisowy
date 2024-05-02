package pl.mpietrewicz.sp.app.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pl.mpietrewicz.sp.app.readmodel.model.Operation;
import pl.mpietrewicz.sp.app.readmodel.model.Type;
import pl.mpietrewicz.sp.ddd.canonicalmodel.publishedlanguage.AggregateId;
import pl.mpietrewicz.sp.ddd.sharedkernel.exception.NotPositiveAmountException;
import pl.mpietrewicz.sp.ddd.sharedkernel.valueobject.PositiveAmount;
import pl.mpietrewicz.sp.modules.balance.application.api.BalanceService;
import pl.mpietrewicz.sp.modules.contract.application.api.ContractService;
import pl.mpietrewicz.sp.modules.contract.application.api.PremiumService;
import pl.mpietrewicz.sp.modules.contract.domain.component.Component;
import pl.mpietrewicz.sp.modules.contract.infrastructure.repo.ComponentRepository;
import pl.mpietrewicz.sp.modules.finance.application.api.FinanceService;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class BasicComponentOperationPerformer implements OperationPerformer {

    private final ContractService contractService;

    private final BalanceService balanceService;

    private final PremiumService premiumService;

    private final ComponentRepository componentRepository;

    private final FinanceService financeService;

    public void perform(Operation operation, AggregateId contractId) throws NotPositiveAmountException {
        String componentName = operation.getComponent();
        LocalDate date = operation.getDate();
        PositiveAmount positiveAmount = operation.getAmount() != null
                ? PositiveAmount.withValue(operation.getAmount())
                : null;

        Type type = operation.getType();
        switch (type) {
            case WPL:
                financeService.addPayment(contractId, positiveAmount, date);
                break;
            case DOF:
                financeService.addSubsidy(contractId, positiveAmount, date);
                break;
            case ZWR:
                balanceService.addRefund(contractId, positiveAmount);
                break;
            case PSU:
                Component component = componentRepository.findBy(contractId, componentName).orElseThrow();
                premiumService.change(component.getAggregateId(), date, positiveAmount);
                break;
            case ZOU:
                contractService.endContract(contractId, date);
                break;
            case WZO:
                contractService.cancelEndContract(contractId);
                break;
            default:
                throw new UnsupportedOperationException();
        }
    }

}