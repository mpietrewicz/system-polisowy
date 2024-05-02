package pl.mpietrewicz.sp.ddd.sharedkernel.valueobject;

import java.math.BigDecimal;

public interface Amount {

    BigDecimal getValue();

    boolean isPositive();

}