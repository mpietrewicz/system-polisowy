package pl.mpietrewicz.sp.modules.contract.application.commands;

import pl.mpietrewicz.sp.cqrs.annotations.Command;
import pl.mpietrewicz.sp.ddd.canonicalmodel.publishedlanguage.AggregateId;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.YearMonth;

@SuppressWarnings("serial")
@Command
public class ChangePremiumCommand implements Serializable {

    private final AggregateId componentId;
    private final BigDecimal premium;
    private final YearMonth changeDate;

    public ChangePremiumCommand(String componentId, BigDecimal premium, YearMonth changeDate) {
        this.componentId = new AggregateId(componentId);
        this.premium = premium;
        this.changeDate = changeDate;
    }

    public AggregateId getComponentId() {
        return componentId;
    }

    public BigDecimal getPremium() {
        return premium;
    }

    public YearMonth getChangeDate() {
        return changeDate;
    }
}