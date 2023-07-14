package pl.mpietrewicz.sp.modules.contract.application.commands;

import lombok.Getter;
import pl.mpietrewicz.sp.cqrs.annotations.Command;
import pl.mpietrewicz.sp.ddd.canonicalmodel.publishedlanguage.AggregateId;

import java.io.Serializable;

@SuppressWarnings("serial")
@Command
public class ShiftAccountingMonthCommand implements Serializable {

    @Getter
    private final AggregateId contractId;

    public ShiftAccountingMonthCommand(String contractId) {
        this.contractId = new AggregateId(contractId);
    }
}