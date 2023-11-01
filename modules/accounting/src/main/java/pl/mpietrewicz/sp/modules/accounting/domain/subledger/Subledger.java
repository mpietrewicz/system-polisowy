package pl.mpietrewicz.sp.modules.accounting.domain.subledger;

import pl.mpietrewicz.sp.ddd.annotations.domain.AggregateRoot;
import pl.mpietrewicz.sp.ddd.support.domain.BaseAggregateRoot;

import javax.persistence.Entity;
import java.time.LocalDate;

@AggregateRoot
@Entity
public class Subledger extends BaseAggregateRoot {

    private LocalDate month;

    public Subledger() {
    }

    public void shiftMonth() {
        this.month = month.plusMonths(1);
    }
}