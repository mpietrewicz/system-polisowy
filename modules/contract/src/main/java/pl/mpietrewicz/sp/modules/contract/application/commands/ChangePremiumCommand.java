package pl.mpietrewicz.sp.modules.contract.application.commands;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import pl.mpietrewicz.sp.cqrs.annotations.Command;
import pl.mpietrewicz.sp.ddd.canonicalmodel.publishedlanguage.AggregateId;
import pl.mpietrewicz.sp.ddd.sharedkernel.valueobject.PositiveAmount;

import java.io.Serializable;
import java.time.LocalDate;

@Command
@Getter
@RequiredArgsConstructor
public class ChangePremiumCommand implements Serializable {

    private final AggregateId componentId;
    private final PositiveAmount premium;
    private final LocalDate changeDate;

}