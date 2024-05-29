package pl.mpietrewicz.sp.app.webui;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import pl.mpietrewicz.sp.app.readmodel.model.Operation;
import pl.mpietrewicz.sp.app.readmodel.model.Result;
import pl.mpietrewicz.sp.app.readmodel.model.Type;
import pl.mpietrewicz.sp.app.service.CalculationResultPreparation;
import pl.mpietrewicz.sp.app.service.ContractCreator;
import pl.mpietrewicz.sp.app.service.OperationPerformer;
import pl.mpietrewicz.sp.app.service.OperationPerformerProvider;
import pl.mpietrewicz.sp.cqrs.command.Gate;
import pl.mpietrewicz.sp.ddd.canonicalmodel.publishedlanguage.AggregateId;
import pl.mpietrewicz.sp.ddd.sharedkernel.exception.NotPositiveAmountException;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@RequiredArgsConstructor
@CrossOrigin(origins = {"${cors-origin}"})
@RestController
public class ProductionController {

    private final Gate gate;

    private final OperationPerformerProvider operationPerformerProvider;

    private final ContractCreator contractCreator;

    private final CalculationResultPreparation calculationResultPreparation;

    @PostMapping("production/calculate")
    public Result calculateOnProductionData(@RequestBody List<Operation> operations)
            throws NotPositiveAmountException {
        Operation createContractOperation = getCreateContractOperation(operations);
        operations.remove(createContractOperation);
        AggregateId contractId = contractCreator.create(createContractOperation);

        for (Operation operation : sort(operations)) {
            OperationPerformer operationPerformer = operationPerformerProvider.get(operation);
            operationPerformer.perform(operation, contractId);
        }

        return calculationResultPreparation.prepare(contractId);
    }

    private Operation getCreateContractOperation(List<Operation> operations) {
        return operations.stream()
                .filter(operation -> operation.getComponent().startsWith("P") || operation.getComponent().startsWith("D"))
                .filter(operation -> operation.getType() == Type.ZUM || operation.getType() == Type.PUM)
                .findAny()
                .orElseThrow();
    }

    private List<Operation> sort(List<Operation> operations) {
        return operations.stream()
                .filter(operation -> operation.getDate() != null)
                .sorted(Comparator.comparing(Operation::getRegistration)
                        .thenComparing(Operation::getDate))
                .collect(Collectors.toList());
    }

}