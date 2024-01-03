package pl.mpietrewicz.sp.modules.contract.application.commands;

import pl.mpietrewicz.sp.cqrs.annotations.Command;
import pl.mpietrewicz.sp.ddd.canonicalmodel.publishedlanguage.AggregateId;
import pl.mpietrewicz.sp.ddd.sharedkernel.Amount;

import java.io.Serializable;
import java.time.YearMonth;

@SuppressWarnings("serial")
@Command
public class ChangePremiumCommand implements Serializable {

    private final AggregateId componentId;
    private final Amount premium;
    private final YearMonth changeDate;

    public ChangePremiumCommand(String componentId, Amount premium, YearMonth changeDate) {
        this.componentId = new AggregateId(componentId);
        this.premium = premium;
        this.changeDate = changeDate;
    }

    public AggregateId getComponentId() {
        return componentId;
    }

    public Amount getPremium() {
        return premium;
    }

    public YearMonth getChangeDate() {
        return changeDate;
    }
}