package pl.mpietrewicz.sp.modules.contract.application.commands.handlers;

import lombok.RequiredArgsConstructor;
import pl.mpietrewicz.sp.cqrs.annotations.CommandHandlerAnnotation;
import pl.mpietrewicz.sp.cqrs.command.handler.CommandHandler;
import pl.mpietrewicz.sp.ddd.canonicalmodel.publishedlanguage.AggregateId;
import pl.mpietrewicz.sp.ddd.sharedkernel.valueobject.Amount;
import pl.mpietrewicz.sp.modules.contract.application.api.ComponentService;
import pl.mpietrewicz.sp.modules.contract.application.commands.AddComponentCommand;

import java.time.LocalDate;

@CommandHandlerAnnotation
@RequiredArgsConstructor
public class AddComponentCommandHandler implements CommandHandler<AddComponentCommand, Void> {

    private final ComponentService componentService;

    @Override
    public Void handle(AddComponentCommand command) {
        AggregateId contractId = command.getContractId();
        String name = command.getName();
        LocalDate registerDate = command.getRegisterDate();
        Amount premium = command.getPremium();

        componentService.addComponent(contractId, name, registerDate, premium);
        return null;
    }
}