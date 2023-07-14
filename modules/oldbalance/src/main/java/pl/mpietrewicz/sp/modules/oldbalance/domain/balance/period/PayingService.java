package pl.mpietrewicz.sp.modules.oldbalance.domain.balance.period;

import java.math.BigDecimal;

public interface PayingService {

    void pay(BigDecimal payment);

    void refund(BigDecimal refund);

}