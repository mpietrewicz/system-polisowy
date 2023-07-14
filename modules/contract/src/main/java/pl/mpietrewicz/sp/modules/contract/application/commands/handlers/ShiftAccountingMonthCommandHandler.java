package pl.mpietrewicz.sp.modules.contract.application.commands.handlers;

import lombok.RequiredArgsConstructor;
import pl.mpietrewicz.sp.cqrs.annotations.CommandHandlerAnnotation;
import pl.mpietrewicz.sp.cqrs.command.handler.CommandHandler;
import pl.mpietrewicz.sp.modules.contract.application.api.ContractService;
import pl.mpietrewicz.sp.modules.contract.application.commands.ShiftAccountingMonthCommand;
import pl.mpietrewicz.sp.ddd.canonicalmodel.publishedlanguage.AggregateId;

@CommandHandlerAnnotation
@RequiredArgsConstructor
public class ShiftAccountingMonthCommandHandler implements CommandHandler<ShiftAccountingMonthCommand, Void> {

    private final ContractService contractService;

    @Override
    public Void handle(ShiftAccountingMonthCommand command) {
        AggregateId contractId = command.getContractId();

        contractService.shiftAccountingMonth(contractId);
        return null;
    }
}