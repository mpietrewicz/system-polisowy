package pl.mpietrewicz.sp.modules.contract.application.commands.handlers;

import lombok.RequiredArgsConstructor;
import pl.mpietrewicz.sp.cqrs.annotations.CommandHandlerAnnotation;
import pl.mpietrewicz.sp.cqrs.command.handler.CommandHandler;
import pl.mpietrewicz.sp.ddd.canonicalmodel.publishedlanguage.AggregateId;
import pl.mpietrewicz.sp.ddd.sharedkernel.valueobject.PositiveAmount;
import pl.mpietrewicz.sp.modules.contract.application.api.PremiumService;
import pl.mpietrewicz.sp.modules.contract.application.commands.ChangePremiumCommand;

import java.time.LocalDate;

@CommandHandlerAnnotation
@RequiredArgsConstructor
public class ChangePremiumCommandHandler implements CommandHandler<ChangePremiumCommand, Void> {

    private final PremiumService premiumService;

    @Override
    public Void handle(ChangePremiumCommand command) {
        AggregateId componentId = command.getComponentId();
        PositiveAmount premium = command.getPremium();
        LocalDate changeDate = command.getChangeDate();

        premiumService.change(componentId, changeDate, premium);
        return null;
    }
}