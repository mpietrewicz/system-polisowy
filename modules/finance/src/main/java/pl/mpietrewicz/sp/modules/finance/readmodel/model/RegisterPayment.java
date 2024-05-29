package pl.mpietrewicz.sp.modules.finance.readmodel.model;

import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Builder
@Getter
public class RegisterPayment {

    private LocalDate date;
    private BigDecimal amount;

}