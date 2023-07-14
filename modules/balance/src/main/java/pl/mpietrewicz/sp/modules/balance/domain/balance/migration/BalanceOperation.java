package pl.mpietrewicz.sp.modules.balance.domain.balance.migration;

import lombok.Builder;
import lombok.Getter;
import pl.mpietrewicz.sp.ddd.canonicalmodel.publishedlanguage.Frequency;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Builder
@Getter
public class BalanceOperation {

    private final OperationType operationType;
    private final LocalDateTime registration;
    private final LocalDate date;
    private final BigDecimal amount;
    private final Frequency frequency;

    public BalanceOperation(OperationType operationType, LocalDateTime registration, LocalDate date, BigDecimal amount, Frequency frequency) {
        this.operationType = operationType;
        this.registration = registration;
        this.date = date;
        this.amount = amount;
        this.frequency = frequency;
    }

}