
package pl.mpietrewicz.sp.modules.finance.application.commands.handlers;

import lombok.RequiredArgsConstructor;
import pl.mpietrewicz.sp.cqrs.annotations.CommandHandlerAnnotation;
import pl.mpietrewicz.sp.cqrs.command.handler.CommandHandler;
import pl.mpietrewicz.sp.ddd.canonicalmodel.publishedlanguage.AggregateId;
import pl.mpietrewicz.sp.ddd.sharedkernel.valueobject.Amount;
import pl.mpietrewicz.sp.modules.finance.application.api.FinanceService;
import pl.mpietrewicz.sp.modules.finance.application.commands.RegisterPaymentCommand;

import java.time.LocalDate;

@CommandHandlerAnnotation
@RequiredArgsConstructor
public class RegisterPaymentCommandHandler implements CommandHandler<RegisterPaymentCommand, Void> {

    private final FinanceService financeService;

    @Override
    public Void handle(RegisterPaymentCommand command) {
        AggregateId contractId = command.getContractId();
        Amount amount = command.getAmount();
        LocalDate date = command.getDate();

        financeService.addPayment(contractId, amount, date);
        return null;
    }
}