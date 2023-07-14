
package pl.mpietrewicz.sp.modules.finance.application.commands;

import pl.mpietrewicz.sp.cqrs.annotations.Command;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;

@SuppressWarnings("serial")
@Command
public class RegisterPaymentCommand implements Serializable {

    private final String contractId;
    private final BigDecimal amount;
    private final LocalDate date;

    public RegisterPaymentCommand(String contractId, BigDecimal amount, LocalDate date) {
        this.contractId = contractId;
        this.amount = amount;
        this.date = date;
    }

    public String getContractId() {
        return contractId;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public LocalDate getDate() {
        return date;
    }
}