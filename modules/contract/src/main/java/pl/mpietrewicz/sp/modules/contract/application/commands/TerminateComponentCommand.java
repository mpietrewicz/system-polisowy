package pl.mpietrewicz.sp.modules.contract.application.commands;

import lombok.Getter;
import pl.mpietrewicz.sp.cqrs.annotations.Command;
import pl.mpietrewicz.sp.ddd.canonicalmodel.publishedlanguage.AggregateId;

import java.io.Serializable;
import java.time.LocalDate;

@SuppressWarnings("serial")
@Command
public class TerminateComponentCommand implements Serializable {

    @Getter
    private final AggregateId componentId;
    @Getter
    private final LocalDate terminatedDate;

    public TerminateComponentCommand(String componentId, LocalDate terminatedDate) {
        this.componentId = new AggregateId(componentId);
        this.terminatedDate = terminatedDate;
    }
}