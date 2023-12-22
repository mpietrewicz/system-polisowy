package pl.mpietrewicz.sp.modules.balance.domain.balance.operation;

import lombok.NoArgsConstructor;
import pl.mpietrewicz.sp.ddd.annotations.domain.DomainEntity;
import pl.mpietrewicz.sp.ddd.canonicalmodel.publishedlanguage.MonthlyBalance;
import pl.mpietrewicz.sp.modules.balance.ddd.support.domain.BaseEntity;
import pl.mpietrewicz.sp.modules.balance.domain.balance.paymentpolicy.Period;
import pl.mpietrewicz.sp.modules.balance.domain.balance.Premium;
import pl.mpietrewicz.sp.modules.balance.domain.balance.operation.type.StartCalculating;

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

@DomainEntity
@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "operation_type", discriminatorType = DiscriminatorType.STRING)
@NoArgsConstructor
public abstract class Operation extends BaseEntity {

    protected LocalDate date;

    private final LocalDateTime registration = LocalDateTime.now();

    @Embedded
    protected Period period;

    @Embedded
    protected Premium premium;

    @Enumerated(EnumType.STRING)
    protected OperationType type;

    protected boolean pending = true;

    protected Operation(LocalDate date) {
        this.date = date;
    }

    public void execute(Operation previousOperation, int grace) {
        this.period = previousOperation.getPeriodCopy();
        this.premium = previousOperation.getPremiumCopy();
        execute();
        period.includeGracePeriod(premium, grace);
        this.pending = false;
    }

    protected abstract void execute();

    public Period getPeriodCopy() {
        return period.createCopy();
    }

    public Premium getPremiumCopy() {
        return premium.createCopy();
    }

    public int orderComparator(Operation operation) {
        if (operation instanceof StartCalculating) return 1;

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
        return period.getMonthlyBalances();
    }

    public boolean isPending() {
        return pending;
    }

}