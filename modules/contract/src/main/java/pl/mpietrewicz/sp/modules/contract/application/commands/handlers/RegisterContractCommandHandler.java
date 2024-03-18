
package pl.mpietrewicz.sp.modules.contract.application.commands.handlers;

import lombok.RequiredArgsConstructor;
import pl.mpietrewicz.sp.cqrs.annotations.CommandHandlerAnnotation;
import pl.mpietrewicz.sp.cqrs.command.handler.CommandHandler;
import pl.mpietrewicz.sp.ddd.canonicalmodel.publishedlanguage.Frequency;
import pl.mpietrewicz.sp.ddd.sharedkernel.valueobject.Amount;
import pl.mpietrewicz.sp.modules.contract.application.api.ContractService;
import pl.mpietrewicz.sp.modules.contract.application.commands.RegisterContractCommand;

import java.time.LocalDate;

@CommandHandlerAnnotation
@RequiredArgsConstructor
public class RegisterContractCommandHandler implements CommandHandler<RegisterContractCommand, Void> {

    private final ContractService contractService;

    @Override
    public Void handle(RegisterContractCommand command) {
        LocalDate start = command.getStart();
        Amount premium = command.getPremium();
        Frequency frequency = command.getFrequency();
        String name = command.getName();

        contractService.createContract(name, start, premium, frequency);
        return null;
    }
}