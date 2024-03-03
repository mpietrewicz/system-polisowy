package pl.mpietrewicz.sp.modules.balance.domain.balance;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import pl.mpietrewicz.sp.ddd.annotations.domain.ValueObject;
import pl.mpietrewicz.sp.ddd.sharedkernel.Amount;
import pl.mpietrewicz.sp.modules.balance.domain.balance.month.Month;

@ValueObject
@Getter
public class MonthToPay {

    public MonthToPay(Month month, Amount amount) {
        this.month = month;
        this.amount = amount;
    }

    private final Month month; // todo: to powinien być miesiąc do spłaty, czyli tylko Unpaid lub Underpaid
    private final Amount amount;

}