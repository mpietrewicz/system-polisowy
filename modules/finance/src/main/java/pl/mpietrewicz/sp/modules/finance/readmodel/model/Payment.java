package pl.mpietrewicz.sp.modules.finance.readmodel.model;

import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Builder
@Getter
public class Payment {

    private LocalDate date;
    private LocalDateTime registration;
    private BigDecimal amount;

}