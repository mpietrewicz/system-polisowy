package pl.mpietrewicz.sp.modules.balance.domain.balance;

import lombok.NoArgsConstructor;
import pl.mpietrewicz.sp.ddd.annotations.domain.ValueObject;
import pl.mpietrewicz.sp.modules.balance.domain.balance.month.ComponentPremium;
import pl.mpietrewicz.sp.modules.balance.domain.balance.month.Month;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import static pl.mpietrewicz.sp.DateUtils.getMonthsBetween;

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

    public YearMonth getLastYearMonth() {
        return months.stream()
                .max(Comparator.comparing(Month::getYearMonth))
                .map(Month::getYearMonth)
                .orElseThrow();
    }

    public YearMonth getLastPaidMonth() {
        return months.stream()
                .filter(Month::isPaid)
                .max(Comparator.comparing(Month::getYearMonth))
                .map(Month::getYearMonth)
                .orElse(getFirstMonth().getYearMonth().minusMonths(1));
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

    public void addNextMonth() {
        Month month = getLastMonth().createNextMonth();
        months.add(month);
    }

    public void addNextMonthWith(ComponentPremium componentPremium) {
        Month lastMonth = getLastMonth();
        List<ComponentPremium> componentPremiums = lastMonth.getComponentPremiums();
        componentPremiums.removeIf(c -> c.isAppliedTo(componentPremium)); // todo: jeśli nie ma takiej składki to zwrócić wyjątek dal ChangePremium
        componentPremiums.add(componentPremium);

        Month month = lastMonth.createNextMonth(componentPremiums);
        months.add(month);
    }

    public List<YearMonth> deleteMonths(YearMonth from) { // todo: można zmienić nazwę na ogranicz / wyznacz okres do miesiąca
        List<YearMonth> deletedMonths = new ArrayList<>();

        while (getLastMonth().getYearMonth().compareTo(from) >= 0) {
            Month lastMonth = getLastMonth();
            lastMonth.invalidate();
            months.remove(lastMonth);
            deletedMonths.add(lastMonth.getYearMonth());
        }
        return deletedMonths;
    }

    public List<Month> getMonths() {
        return months;
    }

    public void includeGracePeriod(int grace) {
        int unpaidMonths = getMonthsBetween(getLastPaidMonth(), getLastYearMonth());

        if (unpaidMonths < grace) {
            extendPeriodToGrace(unpaidMonths, grace);
        } else {
            reducePeriodToGrace(unpaidMonths, grace);
        }
    }

    private void extendPeriodToGrace(int unpaidMonths, int grace) {
        int limit = grace - unpaidMonths;
        while (limit > 0) {
            addNextMonth();
            limit = getLastMonth().isNotPaid() ? limit - 1 : limit;
        }
    }

    private void reducePeriodToGrace(int unpaidMonths, int grace) {
        int monthsToDelete = unpaidMonths - grace - 1;
        deleteMonths(getLastYearMonth().minusMonths(monthsToDelete));
    }

}