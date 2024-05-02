package pl.mpietrewicz.sp.app.readmodel.model;

import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Builder
@Getter
public class Operation {

    private Type type;
    private String component;
    private String registration;
    private LocalDate date;
    private BigDecimal amount;

}