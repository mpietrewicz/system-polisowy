package pl.mpietrewicz.sp.modules.balance.domain.balance.operation;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import pl.mpietrewicz.sp.ddd.annotations.domain.ValueObject;
import pl.mpietrewicz.sp.ddd.sharedkernel.Amount;

import java.time.LocalDate;

@ValueObject
@Getter
@RequiredArgsConstructor
public class PaymentData {

    private final LocalDate date;

    private final Amount amount;

}