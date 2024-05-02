package pl.mpietrewicz.sp.app.readmodel.model;

import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.YearMonth;

@Builder
@Getter
public class Result {

    String contractId;
    YearMonth paidTo;
    BigDecimal excess;

}