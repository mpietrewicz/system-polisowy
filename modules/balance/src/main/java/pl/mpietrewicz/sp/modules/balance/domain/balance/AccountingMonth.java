package pl.mpietrewicz.sp.modules.balance.domain.balance;

import lombok.NoArgsConstructor;
import pl.mpietrewicz.sp.ddd.annotations.domain.ValueObject;
import pl.mpietrewicz.sp.modules.balance.ddd.support.domain.BaseEntity;

import javax.persistence.Entity;
import java.time.YearMonth;

@ValueObject
@Entity
@NoArgsConstructor
public class AccountingMonth extends BaseEntity {

    private YearMonth month;
    private int grace = 3;

    public AccountingMonth(YearMonth month) {
        this.month = month;
    }

    public YearMonth getMonth() {
        return month;
    }

    public boolean isAfter(YearMonth month) {
        return this.month.isAfter(month);
    }

    public int getGrace() {
        return grace;
    }
}