package pl.mpietrewicz.sp.modules.contract.application.commands;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import pl.mpietrewicz.sp.cqrs.annotations.Command;
import pl.mpietrewicz.sp.ddd.canonicalmodel.publishedlanguage.AggregateId;

import java.io.Serializable;
import java.time.LocalDate;

@Command
@Getter
@RequiredArgsConstructor
public class TerminateComponentCommand implements Serializable {

    private final AggregateId componentId;
    private final LocalDate terminatedDate;

}