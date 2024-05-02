package pl.mpietrewicz.sp.app.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pl.mpietrewicz.sp.app.readmodel.model.Operation;
import pl.mpietrewicz.sp.ddd.canonicalmodel.publishedlanguage.AggregateId;
import pl.mpietrewicz.sp.ddd.sharedkernel.exception.NotPositiveAmountException;
import pl.mpietrewicz.sp.ddd.sharedkernel.valueobject.PositiveAmount;
import pl.mpietrewicz.sp.modules.contract.application.api.ContractService;
import pl.mpietrewicz.sp.modules.contract.domain.contract.Contract;

import java.time.LocalDate;

import static pl.mpietrewicz.sp.ddd.canonicalmodel.publishedlanguage.Frequency.QUARTERLY;

@Service
@RequiredArgsConstructor
public class ContractCreator {

    private final ContractService contractService;

    public AggregateId create(Operation createContractOperation)
            throws NotPositiveAmountException {
        String department = "2250";
        String basicComponentName = createContractOperation.getComponent();
        LocalDate date = createContractOperation.getDate();
        PositiveAmount premium = PositiveAmount.withValue(createContractOperation.getAmount());

        Contract contract = contractService.createContract(department, basicComponentName, date, premium, QUARTERLY);
        return contract.getAggregateId();
    }

}