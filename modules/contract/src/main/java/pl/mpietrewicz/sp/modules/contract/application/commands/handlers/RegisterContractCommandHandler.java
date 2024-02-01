
package pl.mpietrewicz.sp.modules.contract.application.commands.handlers;

import lombok.RequiredArgsConstructor;
import pl.mpietrewicz.sp.cqrs.annotations.CommandHandlerAnnotation;
import pl.mpietrewicz.sp.cqrs.command.handler.CommandHandler;
import pl.mpietrewicz.sp.ddd.canonicalmodel.publishedlanguage.Frequency;
import pl.mpietrewicz.sp.ddd.canonicalmodel.publishedlanguage.PaymentPolicyEnum;
import pl.mpietrewicz.sp.ddd.sharedkernel.Amount;
import pl.mpietrewicz.sp.modules.contract.application.api.ContractService;
import pl.mpietrewicz.sp.modules.contract.application.commands.RegisterContractCommand;

import java.time.LocalDate;

@CommandHandlerAnnotation
@RequiredArgsConstructor
public class RegisterContractCommandHandler implements CommandHandler<RegisterContractCommand, Void> {

    private final ContractService contractService;

    @Override
    public Void handle(RegisterContractCommand command) {
        LocalDate registerDate = command.getRegisterDate();
        Amount premium = command.getPremium();
        Frequency frequency = command.getFrequency();
        PaymentPolicyEnum paymentPolicyEnum = command.getPaymentPolicyEnum();
        String number = "todo"; // todo: uzupełnić w przyszłości

        contractService.createContract(number, registerDate, premium, frequency, paymentPolicyEnum);
        return null;
    }
}