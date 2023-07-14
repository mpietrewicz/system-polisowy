package pl.mpietrewicz.sp.modules.oldbalance.domain.utils;

import pl.mpietrewicz.sp.modules.oldbalance.domain.balance.Balance;

import java.math.BigDecimal;
import java.time.YearMonth;

import static org.assertj.core.api.Assertions.assertThat;

public class BalanceAssert {

    private final Balance balance;

    public BalanceAssert(Balance balance) {
        this.balance = balance;
    }

    public void isNotNull() {
        assertThat(balance).isNotNull();
    }

    public void paidToEquals(String month) {
        assertThat(balance.getPaidTo()).isEqualByComparingTo(YearMonth.parse(month));
    }

    public void underpaymentEquals(int amount) {
        assertThat(balance.getUnderpayment()).isEqualByComparingTo(new BigDecimal(amount));
    }

    public void overpaymentEquals(int amount) {
        assertThat(balance.getOverpayment()).isEqualByComparingTo(new BigDecimal(amount));
    }
}