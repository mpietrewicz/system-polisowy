package pl.mpietrewicz.sp.modules.balance.domain.balance;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import pl.mpietrewicz.sp.ddd.annotations.domain.ValueObject;
import pl.mpietrewicz.sp.ddd.sharedkernel.Amount;
import pl.mpietrewicz.sp.modules.balance.domain.balance.month.Month;

@ValueObject
@Getter
@RequiredArgsConstructor
public class MonthToPay {

    private final Month month;
    private final Amount amount;

}