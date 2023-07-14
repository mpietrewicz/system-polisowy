package pl.mpietrewicz.sp.modules.balance.domain.balance;

import lombok.Builder;

import java.math.BigDecimal;
import java.time.YearMonth;

@Builder
public class Allocationlish {

    YearMonth month;
    BigDecimal premium;
    boolean isPaid;

}