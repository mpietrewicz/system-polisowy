package pl.mpietrewicz.sp.modules.balance.domain.balance;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@RequiredArgsConstructor
public class OperationToTestData {

    private final OperationEnum operationEnum;
    private final LocalDate date;
    private final BigDecimal amount;

}