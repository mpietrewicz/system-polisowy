package pl.mpietrewicz.sp;

import java.time.LocalDate;
import java.time.Period;
import java.time.YearMonth;
import java.time.temporal.ChronoUnit;

public class DateUtils {

    public static LocalDate pierwszyDzienMiesiaca(YearMonth yearMonth) {
        return yearMonth.atDay(1);
    }

    public static LocalDate pierwszyDzienMiesiaca(LocalDate localDate) {
        YearMonth yearMonth = YearMonth.from(localDate);
        return yearMonth.atDay(1);
    }

    public static LocalDate pierwszyDzienKolejnegoMiesiaca(LocalDate localDate) {
        LocalDate localDatePlusOneMonth = localDate.plusMonths(1);
        return pierwszyDzienMiesiaca(localDatePlusOneMonth);
    }

    public static LocalDate ostatniDzienMiesiaca(LocalDate localDate) {
        YearMonth yearMonth = YearMonth.from(localDate);
        return yearMonth.atEndOfMonth();
    }

    public static LocalDate ostatniDzienPoprzedniegoMiesiaca(LocalDate localDate) {
        YearMonth yearMonth = YearMonth.from(localDate);
        return yearMonth.minus(Period.ofMonths(1)).atEndOfMonth();
    }

    public static LocalDate biezacaData() {
        return LocalDate.now();
    }

    public static YearMonth biezacyMiesiac() {
        return YearMonth.now();
    }

    public static int getMonthsBetween(YearMonth from, YearMonth to) {
        return Long.valueOf(ChronoUnit.MONTHS.between(from, to)).intValue();
    }

}