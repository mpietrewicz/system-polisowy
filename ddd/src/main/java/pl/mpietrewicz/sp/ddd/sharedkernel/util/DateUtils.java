package pl.mpietrewicz.sp.ddd.sharedkernel.util;

import java.time.YearMonth;
import java.time.temporal.ChronoUnit;

public class DateUtils {

    public static int getMonthsBetween(YearMonth from, YearMonth to) {
        return Long.valueOf(ChronoUnit.MONTHS.between(from, to)).intValue();
    }

}