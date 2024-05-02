
package pl.mpietrewicz.sp.modules.finance.application.commands;

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
public class RegisterPaymentCommand implements Serializable {

    private final AggregateId contractId;
    private final PositiveAmount payment;
    private final LocalDate date;

}