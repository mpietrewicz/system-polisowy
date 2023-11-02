package pl.mpietrewicz.sp.modules.balance.domain.balance;

import lombok.NoArgsConstructor;
import pl.mpietrewicz.sp.ddd.annotations.domain.ValueObject;
import pl.mpietrewicz.sp.ddd.canonicalmodel.publishedlanguage.Frequency;
import pl.mpietrewicz.sp.ddd.canonicalmodel.publishedlanguage.MonthlyBalance;
import pl.mpietrewicz.sp.modules.balance.ddd.support.domain.BaseEntity;

import javax.persistence.DiscriminatorColumn;
import javax.persistence.DiscriminatorType;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@ValueObject
@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "operation_type", discriminatorType = DiscriminatorType.STRING)
@NoArgsConstructor
public abstract class Operation extends BaseEntity {

    protected LocalDate date;

    private final LocalDateTime registration = LocalDateTime.now();

    @Embedded
    protected Period period;

    @Enumerated(EnumType.STRING)
    protected OperationType type;

    protected boolean pending = true;

    protected Operation(LocalDate date) {
        this.date = date;
    }

    public void calculate(Operation previousOperation) {
        this.period = previousOperation.getPeriodCopy();
        calculate();

        AccountingMonth accountingMonth = new AccountingMonth(YearMonth.from(date)); // todo: to powinna byc prawdziwa data miesiąca ksiegowego

        period.includeGracePeriod(accountingMonth);
        this.pending = false;
    }

    protected abstract void calculate();

    public Period getPeriodCopy() {
        return period.returnCopy();
    }

    public Optional<Frequency> getFrequency() {
        return Optional.empty();
    }

    public int orderComparator(Operation operation) {
        int dateComparator = this.date.compareTo(operation.date);
        if (dateComparator != 0) {
            return dateComparator;
        } else {
            int priorityComparator = this.getPriority().compareTo(operation.getPriority());
            if (priorityComparator == 0) {
                return this.registration.compareTo(operation.registration);
            } else {
                return priorityComparator;
            }
        }
    }

    protected Integer getPriority() {
        return 1;
    }

    public boolean isAfter(Operation operation) {
        return orderComparator(operation) > 0;
    }

    public boolean isBefore(Operation operation) {
        return orderComparator(operation) < 0;
    }

    public LocalDate getDate() {
        return date;
    }

    public YearMonth getMonth() {
        return YearMonth.from(date);
    }

    public List<MonthlyBalance> getMonthlyBalances() {
        return period.getMonths().stream()
                .map(month -> MonthlyBalance.builder()
                        .month(month.getYearMonth())
                        .componentPremiums(month.getPremiumComponents())
                        .isPaid(month.isPaid())
                        .build())
                .collect(Collectors.toList());
    }

    public boolean isPending() {
        return pending;
    }

    public boolean isStartCalculatingOperation() {
        return type == OperationType.START_CALCULATING;
    }

}