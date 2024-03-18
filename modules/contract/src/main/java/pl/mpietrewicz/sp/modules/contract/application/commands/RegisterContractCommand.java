
package pl.mpietrewicz.sp.modules.contract.application.commands;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import pl.mpietrewicz.sp.cqrs.annotations.Command;
import pl.mpietrewicz.sp.ddd.canonicalmodel.publishedlanguage.Frequency;
import pl.mpietrewicz.sp.ddd.sharedkernel.valueobject.Amount;

import java.io.Serializable;
import java.time.LocalDate;

@Command
@Getter
@RequiredArgsConstructor
public class RegisterContractCommand implements Serializable {

    private final LocalDate start;
    private final String name;
    private final Amount premium;
    private final Frequency frequency;

}