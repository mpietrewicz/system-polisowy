package pl.mpietrewicz.sp.modules.contract.application.commands;

import pl.mpietrewicz.sp.cqrs.annotations.Command;
import pl.mpietrewicz.sp.ddd.canonicalmodel.publishedlanguage.AggregateId;
import pl.mpietrewicz.sp.ddd.sharedkernel.Amount;

import java.io.Serializable;
import java.time.LocalDate;

@SuppressWarnings("serial")
@Command
public class AddComponentCommand implements Serializable {

    private final AggregateId contractId;
    private final LocalDate registerDate;
    private final Amount premium;

    public AddComponentCommand(String contractId, LocalDate registerDate, Amount premium) {
        this.contractId = new AggregateId(contractId);
        this.registerDate = registerDate;
        this.premium = premium;
    }

    public AggregateId getContractId() {
        return contractId;
    }

    public LocalDate getRegisterDate() {
        return registerDate;
    }

    public Amount getPremium() {
        return premium;
    }
}