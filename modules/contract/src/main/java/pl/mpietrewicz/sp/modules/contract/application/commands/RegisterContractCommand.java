
package pl.mpietrewicz.sp.modules.contract.application.commands;

import lombok.Getter;
import pl.mpietrewicz.sp.cqrs.annotations.Command;
import pl.mpietrewicz.sp.ddd.canonicalmodel.publishedlanguage.Frequency;
import pl.mpietrewicz.sp.ddd.canonicalmodel.publishedlanguage.PaymentPolicy;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;

@SuppressWarnings("serial")
@Command
@Getter
public class RegisterContractCommand implements Serializable {

    private final LocalDate registerDate;
    private final BigDecimal premium;
    private final Frequency frequency;
    private final PaymentPolicy paymentPolicy;

    public RegisterContractCommand(LocalDate registerDate, BigDecimal premium, Frequency frequency, PaymentPolicy paymentPolicy) {
        this.registerDate = registerDate;
        this.premium = premium;
        this.frequency = frequency;
        this.paymentPolicy = paymentPolicy;
    }

}