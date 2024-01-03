
package pl.mpietrewicz.sp.modules.finance.application.commands.handlers;

import lombok.RequiredArgsConstructor;
import pl.mpietrewicz.sp.cqrs.annotations.CommandHandlerAnnotation;
import pl.mpietrewicz.sp.cqrs.command.handler.CommandHandler;
import pl.mpietrewicz.sp.ddd.sharedkernel.Amount;
import pl.mpietrewicz.sp.modules.finance.application.api.PaymentService;
import pl.mpietrewicz.sp.modules.finance.application.commands.RegisterPaymentCommand;

import java.time.LocalDate;

@CommandHandlerAnnotation
@RequiredArgsConstructor
public class RegisterPaymentCommandHandler implements CommandHandler<RegisterPaymentCommand, Void> {

    private final PaymentService paymentService;

    @Override
    public Void handle(RegisterPaymentCommand command) {
        String contractId = command.getContractId();
        Amount amount = command.getAmount();
        LocalDate date = command.getDate();

        paymentService.addPayment(contractId, amount, date);
        return null;
    }
}