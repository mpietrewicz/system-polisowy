package pl.mpietrewicz.sp.modules.balance.domain.balance.operation;

import lombok.NoArgsConstructor;
import pl.mpietrewicz.sp.ddd.annotations.domain.DomainEntity;
import pl.mpietrewicz.sp.ddd.canonicalmodel.publishedlanguage.MonthlyBalance;
import pl.mpietrewicz.sp.ddd.canonicalmodel.publishedlanguage.snapshot.premium.PremiumSnapshot;
import pl.mpietrewicz.sp.ddd.support.domain.DomainEventPublisher;
import pl.mpietrewicz.sp.ddd.support.infrastructure.repo.BaseEntity;
import pl.mpietrewicz.sp.modules.balance.domain.balance.Period;
import pl.mpietrewicz.sp.modules.balance.domain.balance.operation.type.StartCalculating;
import pl.mpietrewicz.sp.modules.balance.exceptions.ReexecutionException;

import javax.persistence.CascadeType;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.DiscriminatorType;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;

@DomainEntity
@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "operation_type", discriminatorType = DiscriminatorType.STRING)
@NoArgsConstructor
public abstract class Operation extends BaseEntity {

    protected LocalDate date;

    private final LocalDateTime registration = LocalDateTime.now();

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "operation_id")
    protected List<Period> periods = new ArrayList<>();

    @Enumerated(EnumType.STRING)
    protected OperationType type;

    protected Operation(LocalDate date) {
        this.date = date;
    }

    public void reexecute(Operation previousOperation, PremiumSnapshot premiumSnapshot, DomainEventPublisher eventPublisher)
            throws ReexecutionException {
        getCurrentPeriod().markAsFormer();
        periods.add(previousOperation.getPeriodCopy());
        reexecute(premiumSnapshot, eventPublisher);
    }

    public void execute(Operation previousOperation, PremiumSnapshot premiumSnapshot, DomainEventPublisher eventPublisher) {
        periods.add(previousOperation.getPeriodCopy());
        execute(premiumSnapshot, eventPublisher);
    }

    protected abstract void execute(PremiumSnapshot premiumSnapshot, DomainEventPublisher eventPublisher);

    protected abstract void reexecute(PremiumSnapshot premiumSnapshot, DomainEventPublisher eventPublisher)
            throws ReexecutionException;

    public Period getPeriodCopy() {
        return getCurrentPeriod().createCopy();
    }

    protected Period getCurrentPeriod() {
        return periods.stream()
                .filter(Period::isCurrent)
                .findFirst()
                .orElseThrow();
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
        return getCurrentPeriod().getMonthlyBalances(premiumSnapshot);
    }

    public LocalDateTime getRegistration() {
        return registration;
    }

    public abstract void handle(ReexecutionException e, DomainEventPublisher eventPublisher);

}