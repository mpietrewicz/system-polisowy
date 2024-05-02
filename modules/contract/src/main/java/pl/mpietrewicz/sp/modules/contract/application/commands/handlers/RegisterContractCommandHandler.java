
package pl.mpietrewicz.sp.modules.contract.application.commands.handlers;

import lombok.RequiredArgsConstructor;
import pl.mpietrewicz.sp.cqrs.annotations.CommandHandlerAnnotation;
import pl.mpietrewicz.sp.cqrs.command.handler.CommandHandler;
import pl.mpietrewicz.sp.ddd.canonicalmodel.publishedlanguage.Frequency;
import pl.mpietrewicz.sp.ddd.sharedkernel.valueobject.PositiveAmount;
import pl.mpietrewicz.sp.modules.contract.application.api.ContractService;
import pl.mpietrewicz.sp.modules.contract.application.commands.RegisterContractCommand;

import java.time.LocalDate;

@CommandHandlerAnnotation
@RequiredArgsConstructor
public class RegisterContractCommandHandler implements CommandHandler<RegisterContractCommand, Void> {

    private final ContractService contractService;

    @Override
    public Void handle(RegisterContractCommand command) {
        String name = command.getName();
        String department = command.getDepartment();
        LocalDate start = command.getStart();
        PositiveAmount premium = command.getPremium();
        Frequency frequency = command.getFrequency();

        contractService.createContract(department, name, start, premium, frequency);
        return null;
    }
}