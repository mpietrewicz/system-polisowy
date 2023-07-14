package pl.mpietrewicz.sp.modules.balance.domain.balance;

import lombok.NoArgsConstructor;
import pl.mpietrewicz.sp.ddd.annotations.domain.ValueObject;
import pl.mpietrewicz.sp.modules.balance.domain.balance.month.Month;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import java.math.BigDecimal;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@ValueObject
@Embeddable
@NoArgsConstructor
public class Period {

    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "operation_id")
    private List<Month> months;

    public Period(List<Month> months) {
        this.months = months;
    }

    public Month getLastMonth() {
        return months.stream()
                .max(Comparator.comparing(Month::getYearMonth))
                .orElseThrow();
    }

    public Month getFirstMonth() {
        return months.stream()
                .min(Comparator.comparing(Month::getYearMonth))
                .orElseThrow();
    }

    public Period returnCopy() {
        List<Month> copiedMonths = new ArrayList<>();
        Month previous = null;
        Month next;
        for (Month month : months) {
            next = month.createCopy();
            if (previous != null) previous.setNext(next);
            next.setPervious(previous);
            previous = next;
            copiedMonths.add(next);
        }
        return new Period(copiedMonths);
    }

    public void addMonth() {
        Month month = getLastMonth().createNextMonth();
        months.add(month);
    }

    public void addMonth(BigDecimal premium) {
        Month month = getLastMonth().createNextMonth(premium);
        months.add(month);
    }

    public void deleteMonths(YearMonth from) {
        while (getLastMonth().getYearMonth().compareTo(from) >= 0) {
            Month lastMonth = getLastMonth();
            lastMonth.invalidate();
            months.remove(lastMonth);
        }
    }

    public List<Month> getMonths() {
        return months;
    }

}