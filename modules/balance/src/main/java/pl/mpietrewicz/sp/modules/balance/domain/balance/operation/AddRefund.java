package pl.mpietrewicz.sp.modules.balance.domain.balance.operation;

import lombok.NoArgsConstructor;
import pl.mpietrewicz.sp.ddd.annotations.domain.ValueObject;
import pl.mpietrewicz.sp.modules.balance.domain.balance.month.Month;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import java.math.BigDecimal;
import java.time.LocalDate;

import static pl.mpietrewicz.sp.modules.balance.domain.balance.OperationType.ADD_REFUND;

@ValueObject
@Entity
@DiscriminatorValue("ADD_REFUND")
@NoArgsConstructor
public class AddRefund extends Operation {

    private BigDecimal amount;

    public AddRefund(LocalDate date, BigDecimal amount) {
        super(date);
        this.amount = amount;
        this.type = ADD_REFUND;
    }

    @Override
    public void execute() {
        Month month = period.getLastMonth(); // todo: zdejmowanie wpłat powinno skutkować też usutaniem miesiacy nieopłaconych
        month.tryRefund(amount);
        this.type = ADD_REFUND;
    }

}