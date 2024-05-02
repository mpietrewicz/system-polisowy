package pl.mpietrewicz.sp.modules.balance.readmodel.model;

import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Builder
@Getter
public class Balance {

    private LocalDate validAt;
    private BigDecimal balance;

}