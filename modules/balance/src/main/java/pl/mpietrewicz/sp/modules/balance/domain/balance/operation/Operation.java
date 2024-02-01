package pl.mpietrewicz.sp.modules.balance.domain.balance.operation;

import lombok.NoArgsConstructor;
import pl.mpietrewicz.sp.ddd.annotations.domain.DomainEntity;
import pl.mpietrewicz.sp.ddd.canonicalmodel.publishedlanguage.MonthlyBalance;
import pl.mpietrewicz.sp.ddd.canonicalmodel.publishedlanguage.snapshot.premium.PremiumSnapshot;
import pl.mpietrewicz.sp.modules.balance.ddd.support.domain.BaseEntity;
import pl.mpietrewicz.sp.modules.balance.domain.balance.operation.type.StartCalculating;
import pl.mpietrewicz.sp.modules.balance.domain.balance.Period;

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

    @Enumerated(EnumType.STRING)
    protected OperationType type;

    protected boolean pending = true;

    protected Operation(LocalDate date) {
        this.date = date;
    }

    public void execute(Operation previousOperation, PremiumSnapshot premiumSnapshot) {
        if (period != null) {
            period.clear(); // todo: jeśli coś jest to znaczy że ponwonie przeliczam, wiec mogę gdzieś odłożyć sobię historią, albo wydzielić do dwóch metod execute i reexecute
            period.months.addAll(previousOperation.getPeriodCopy().months);
        } else {
            period = previousOperation.getPeriodCopy();
        }
        execute(premiumSnapshot);
        this.pending = false;
    }

    protected abstract void execute(PremiumSnapshot premiumSnapshot);

    public Period getPeriodCopy() {
        return period.createCopy();
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

    public List<MonthlyBalance> getMonthlyBalances(PremiumSnapshot premiumSnapshot) {
        return period.getMonthlyBalances(premiumSnapshot);
    }

    public boolean isPending() {
        return pending;
    }

    public LocalDateTime getRegistration() {
        return registration;
    }
}