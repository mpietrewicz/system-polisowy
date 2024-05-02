
package pl.mpietrewicz.sp.modules.contract.application.commands;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import pl.mpietrewicz.sp.cqrs.annotations.Command;
import pl.mpietrewicz.sp.ddd.canonicalmodel.publishedlanguage.Frequency;
import pl.mpietrewicz.sp.ddd.sharedkernel.valueobject.PositiveAmount;

import java.io.Serializable;
import java.time.LocalDate;

@Command
@Getter
@RequiredArgsConstructor
public class RegisterContractCommand implements Serializable {

    private final String department;
    private final String name;
    private final LocalDate start;
    private final PositiveAmount premium;
    private final Frequency frequency;

}