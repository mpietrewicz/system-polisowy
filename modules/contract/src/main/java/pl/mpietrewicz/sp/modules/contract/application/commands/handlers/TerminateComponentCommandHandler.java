
package pl.mpietrewicz.sp.modules.contract.application.commands.handlers;

import lombok.RequiredArgsConstructor;
import pl.mpietrewicz.sp.cqrs.annotations.CommandHandlerAnnotation;
import pl.mpietrewicz.sp.cqrs.command.handler.CommandHandler;
import pl.mpietrewicz.sp.modules.contract.application.api.ComponentService;
import pl.mpietrewicz.sp.modules.contract.application.commands.TerminateComponentCommand;
import pl.mpietrewicz.sp.ddd.canonicalmodel.publishedlanguage.AggregateId;

import java.time.LocalDate;

@CommandHandlerAnnotation
@RequiredArgsConstructor
public class TerminateComponentCommandHandler implements CommandHandler<TerminateComponentCommand, Void> {

    private final ComponentService componentService;

    @Override
    public Void handle(TerminateComponentCommand command) {
        AggregateId componentId = command.getComponentId();
        LocalDate terminatedDate = command.getTerminatedDate();
        String number = "todo"; // todo: uzupełnić w przyszłości

        componentService.terminate(number, terminatedDate);
        return null;
    }
}