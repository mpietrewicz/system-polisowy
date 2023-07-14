package pl.mpietrewicz.sp.modules.accounting.domain.accounting;

import pl.mpietrewicz.sp.ddd.annotations.domain.AggregateRoot;
import pl.mpietrewicz.sp.ddd.support.domain.BaseAggregateRoot;

import javax.persistence.Entity;
import java.time.LocalDate;

@AggregateRoot
@Entity
public class Accounting extends BaseAggregateRoot {

    private LocalDate month;

    public Accounting() {
    }

    public void shiftMonth() {
        this.month = month.plusMonths(1);
    }
}