package pl.mpietrewicz.sp.modules.balance.domain.balance.migration;

import lombok.Builder;
import lombok.Getter;

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

    public BalanceOperation(OperationType operationType, LocalDateTime registration, LocalDate date, BigDecimal amount) {
        this.operationType = operationType;
        this.registration = registration;
        this.date = date;
        this.amount = amount;
    }

}