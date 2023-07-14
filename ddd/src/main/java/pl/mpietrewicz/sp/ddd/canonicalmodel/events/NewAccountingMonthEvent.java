package pl.mpietrewicz.sp.ddd.canonicalmodel.events;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import pl.mpietrewicz.sp.ddd.annotations.event.Event;

import java.io.Serializable;
import java.time.YearMonth;

@SuppressWarnings("serial")
@Event
@Getter
@RequiredArgsConstructor
public class NewAccountingMonthEvent implements Serializable {

    private final YearMonth month;

}