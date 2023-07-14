
package pl.mpietrewicz.sp.modules.contract.application.commands;

import pl.mpietrewicz.sp.cqrs.annotations.Command;
import pl.mpietrewicz.sp.ddd.canonicalmodel.publishedlanguage.Frequency;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;

@SuppressWarnings("serial")
@Command
public class RegisterContractCommand implements Serializable {

    private final LocalDate registerDate;
    private final BigDecimal premium;
    private final Frequency frequency;

    public RegisterContractCommand(LocalDate registerDate, BigDecimal premium, Frequency frequency) {
        this.registerDate = registerDate;
        this.premium = premium;
        this.frequency = frequency;
    }

    public LocalDate getRegisterDate() {
        return registerDate;
    }

    public BigDecimal getPremium() {
        return premium;
    }

    public Frequency getFrequency() {
        return frequency;
    }
}