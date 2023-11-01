
package pl.mpietrewicz.sp.modules.accounting.application.commands.handlers;

import lombok.RequiredArgsConstructor;
import pl.mpietrewicz.sp.cqrs.annotations.CommandHandlerAnnotation;
import pl.mpietrewicz.sp.cqrs.command.handler.CommandHandler;
import pl.mpietrewicz.sp.modules.accounting.application.api.SubledgerService;
import pl.mpietrewicz.sp.modules.accounting.application.commands.GenerateInterfaceCommand;

@CommandHandlerAnnotation
@RequiredArgsConstructor
public class GenerateInterfaceCommandHandler implements CommandHandler<GenerateInterfaceCommand, Void> {

    private final SubledgerService subledgerService;

    @Override
    public Void handle(GenerateInterfaceCommand command) {
        subledgerService.generateInterface();
        return null;
    }
}