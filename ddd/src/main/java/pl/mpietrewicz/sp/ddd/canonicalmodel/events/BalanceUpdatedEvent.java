package pl.mpietrewicz.sp.ddd.canonicalmodel.events;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import pl.mpietrewicz.sp.ddd.annotations.event.Event;
import pl.mpietrewicz.sp.ddd.canonicalmodel.publishedlanguage.AggregateId;
import pl.mpietrewicz.sp.ddd.canonicalmodel.publishedlanguage.MonthlyBalance;

import java.io.Serializable;
import java.util.List;

@Event(boundedContext = "balance")
@Getter
@RequiredArgsConstructor
public class BalanceUpdatedEvent implements Serializable {

    private final transient AggregateId contractId;
    private final transient List<MonthlyBalance> monthlyBalances;

}