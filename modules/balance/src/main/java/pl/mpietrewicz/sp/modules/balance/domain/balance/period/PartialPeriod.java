package pl.mpietrewicz.sp.modules.balance.domain.balance.period;

import lombok.Getter;
import lombok.NoArgsConstructor;
import pl.mpietrewicz.sp.ddd.canonicalmodel.publishedlanguage.MonthlyBalance;
import pl.mpietrewicz.sp.ddd.support.infrastructure.repo.BaseEntity;
import pl.mpietrewicz.sp.modules.balance.domain.balance.month.ChangeStatus;
import pl.mpietrewicz.sp.modules.balance.domain.balance.month.Month;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import java.util.stream.Collectors;

@Entity
@Getter
@NoArgsConstructor
public class PartialPeriod extends BaseEntity implements PeriodProvider {

    private LocalDate start;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "partial_period_id")
    private List<Month> months;

    private boolean isValid;

    private String info;

    public PartialPeriod(LocalDate start, List<Month> months, boolean isValid, String info) {
        this.start = start;
        this.months = months;
        this.isValid = isValid;
        this.info = info;
    }

    public void markAsInvalid() {
        this.isValid = false;
    }

    public boolean isValid() {
        return isValid;
    }

    @Override
    public List<MonthlyBalance> getMonthlyBalances() {
        return months.stream()
                .map(month -> MonthlyBalance.builder()
                        .month(month.getYearMonth())
                        .event(getEvent(month.getChangeStatus()))
                        .premium(month.getPremium().getValue())
                        .paid(month.getPaid().getValue())
                        .build()
                ).collect(Collectors.toList());
    }

    @Override
    public List<YearMonth> getRenewalMonths() {
        return months.stream()
                .filter(Month::isRenewal)
                .map(Month::getYearMonth)
                .collect(Collectors.toList());
    }

    private String getEvent(ChangeStatus changeStatus) {
        switch (changeStatus) {
            case ADDED:
                return "added";
            case CHANGED:
                return "changed";
            case REMOVED:
                return "removed";
            default:
                throw new IllegalArgumentException();
        }
    }

}