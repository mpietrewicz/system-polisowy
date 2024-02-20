package pl.mpietrewicz.sp.ddd.canonicalmodel.events;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import pl.mpietrewicz.sp.ddd.annotations.event.Event;
import pl.mpietrewicz.sp.ddd.canonicalmodel.publishedlanguage.MonthlyBalance;
import pl.mpietrewicz.sp.ddd.canonicalmodel.publishedlanguage.snapshot.ContractData;

import java.io.Serializable;
import java.util.List;

@SuppressWarnings("serial")
@Event
@Getter
@RequiredArgsConstructor
public class BalanceUpdatedEvent implements Serializable {

    private final transient ContractData contractData;
    private final transient List<MonthlyBalance> monthlyBalances;

}