package pl.mpietrewicz.sp.ddd.canonicalmodel.publishedlanguage;

import java.security.InvalidParameterException;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;

public enum Frequency {

    MONTHLY(1),
    QUARTERLY(3),
    SEMI_ANNUALLY(6),
    ANNUALLY(12);

    private final int numberOfMonths;

    Frequency(int numberOfMonths) {
        this.numberOfMonths = numberOfMonths;
    }

    public List<YearMonth> getMonths(YearMonth month) {
        return getMonths(month, month);
    }

    public List<YearMonth> getMonths(YearMonth from, YearMonth to) {
        if (from.compareTo(to) > 0) {
            throw new InvalidParameterException("Invalid period since - to parameters!");
        }
        List<YearMonth> months = new ArrayList<>();
        YearMonth month = from.minusMonths(1);
        do {
            for (int i = 0; i < numberOfMonths; i++) {
                month = month.plusMonths(1);
                months.add(month);
            }
        } while (month.compareTo(to) < 0);

        return months;
    }

}