package pl.mpietrewicz.sp.modules.contract.application.commands.handlers;

import lombok.RequiredArgsConstructor;
import pl.mpietrewicz.sp.modules.contract.application.api.PremiumService;
import pl.mpietrewicz.sp.modules.contract.application.commands.ChangePremiumCommand;
import pl.mpietrewicz.sp.cqrs.annotations.CommandHandlerAnnotation;
import pl.mpietrewicz.sp.cqrs.command.handler.CommandHandler;
import pl.mpietrewicz.sp.ddd.canonicalmodel.publishedlanguage.AggregateId;

import java.math.BigDecimal;
import java.time.YearMonth;

@CommandHandlerAnnotation
@RequiredArgsConstructor
public class ChangePremiumCommandHandler implements CommandHandler<ChangePremiumCommand, Void> {

    private final PremiumService premiumService;

    @Override
    public Void handle(ChangePremiumCommand command) {
        AggregateId componentId = command.getComponentId();
        BigDecimal premium = command.getPremium();
        YearMonth changeDate = command.getChangeDate();

        premiumService.change(componentId, changeDate.atDay(1), premium);
        return null;
    }
}