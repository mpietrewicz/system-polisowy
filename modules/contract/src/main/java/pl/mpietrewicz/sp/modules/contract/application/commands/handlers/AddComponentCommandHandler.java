package pl.mpietrewicz.sp.modules.contract.application.commands.handlers;

import lombok.RequiredArgsConstructor;
import pl.mpietrewicz.sp.cqrs.annotations.CommandHandlerAnnotation;
import pl.mpietrewicz.sp.cqrs.command.handler.CommandHandler;
import pl.mpietrewicz.sp.modules.contract.application.api.ComponentService;
import pl.mpietrewicz.sp.modules.contract.application.commands.AddComponentCommand;
import pl.mpietrewicz.sp.ddd.canonicalmodel.publishedlanguage.AggregateId;

import java.math.BigDecimal;
import java.time.LocalDate;

@CommandHandlerAnnotation
@RequiredArgsConstructor
public class AddComponentCommandHandler implements CommandHandler<AddComponentCommand, Void> {

    private final ComponentService componentService;

    @Override
    public Void handle(AddComponentCommand command) {
        AggregateId contractId = command.getContractId();
        LocalDate registerDate = command.getRegisterDate();
        BigDecimal premium = command.getPremium();

        componentService.addComponent(contractId, registerDate, premium);
        return null;
    }
}