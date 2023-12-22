
package pl.mpietrewicz.sp.modules.contract.application.commands;

import lombok.Getter;
import pl.mpietrewicz.sp.cqrs.annotations.Command;
import pl.mpietrewicz.sp.ddd.canonicalmodel.publishedlanguage.Frequency;
import pl.mpietrewicz.sp.ddd.canonicalmodel.publishedlanguage.PaymentPolicyEnum;
import pl.mpietrewicz.sp.ddd.sharedkernel.Amount;

import java.io.Serializable;
import java.time.LocalDate;

@SuppressWarnings("serial")
@Command
@Getter
public class RegisterContractCommand implements Serializable {

    private final LocalDate registerDate;
    private final Amount premium;
    private final Frequency frequency;
    private final PaymentPolicyEnum paymentPolicyEnum;

    public RegisterContractCommand(LocalDate registerDate, Amount premium, Frequency frequency, PaymentPolicyEnum paymentPolicyEnum) {
        this.registerDate = registerDate;
        this.premium = premium;
        this.frequency = frequency;
        this.paymentPolicyEnum = paymentPolicyEnum;
    }

}