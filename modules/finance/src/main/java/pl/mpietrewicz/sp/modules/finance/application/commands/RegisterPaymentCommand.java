
package pl.mpietrewicz.sp.modules.finance.application.commands;

import pl.mpietrewicz.sp.cqrs.annotations.Command;
import pl.mpietrewicz.sp.ddd.sharedkernel.Amount;

import java.io.Serializable;
import java.time.LocalDate;

@SuppressWarnings("serial")
@Command
public class RegisterPaymentCommand implements Serializable {

    private final String contractId;
    private final Amount amount;
    private final LocalDate date;

    public RegisterPaymentCommand(String contractId, Amount amount, LocalDate date) {
        this.contractId = contractId;
        this.amount = amount;
        this.date = date;
    }

    public String getContractId() {
        return contractId;
    }

    public Amount getAmount() {
        return amount;
    }

    public LocalDate getDate() {
        return date;
    }
}