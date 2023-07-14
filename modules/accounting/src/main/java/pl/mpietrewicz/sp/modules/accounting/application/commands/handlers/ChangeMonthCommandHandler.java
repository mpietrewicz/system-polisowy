
package pl.mpietrewicz.sp.modules.accounting.application.commands.handlers;

import lombok.RequiredArgsConstructor;
import pl.mpietrewicz.sp.cqrs.annotations.CommandHandlerAnnotation;
import pl.mpietrewicz.sp.cqrs.command.handler.CommandHandler;
import pl.mpietrewicz.sp.modules.accounting.application.api.AccountingService;
import pl.mpietrewicz.sp.modules.accounting.application.commands.ChangeMonthCommand;

@CommandHandlerAnnotation
@RequiredArgsConstructor
public class ChangeMonthCommandHandler implements CommandHandler<ChangeMonthCommand, Void> {

    private final AccountingService accountingService;

    @Override
    public Void handle(ChangeMonthCommand command) {
        accountingService.openNewMonth();
        return null;
    }
}