package pl.mpietrewicz.sp;

import pl.mpietrewicz.sp.ddd.annotations.domain.PublishedLanguage;

import java.time.YearMonth;

@PublishedLanguage
public class SystemParameters {

    public static final YearMonth CURRENT_ACCOUNTING_MONTH = YearMonth.parse("2023-04"); // todo: pobierać z bazy danych

    public static YearMonth getCurrentAccountingMonth() {
        return CURRENT_ACCOUNTING_MONTH;
    }

}