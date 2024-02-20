package pl.mpietrewicz.sp.modules.balance.domain.balance.operation;

import lombok.Getter;
import pl.mpietrewicz.sp.ddd.canonicalmodel.publishedlanguage.MonthlyBalance;
import pl.mpietrewicz.sp.ddd.canonicalmodel.publishedlanguage.snapshot.premium.PremiumSnapshot;
import pl.mpietrewicz.sp.ddd.support.domain.DomainEventPublisher;
import pl.mpietrewicz.sp.modules.balance.domain.balance.Period;
import pl.mpietrewicz.sp.modules.balance.domain.balance.operation.type.StartCalculating;
import pl.mpietrewicz.sp.modules.balance.exceptions.ReexecutionException;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;

@Getter
public abstract class Operation {

    private Long id;

    protected final LocalDate date;

    private final LocalDateTime registration = LocalDateTime.now();

    protected final List<Period> periods;

    @Enumerated(EnumType.STRING)
    protected OperationType type;

    protected Operation(LocalDate date) {
        this.date = date;
        this.periods = new ArrayList<>();
    }

    protected Operation(Long id, LocalDate date, List<Period> periods) {
        this.id = id;
        this.date = date;
        this.periods = periods;
    }

    public void reexecute(Operation previousOperation, PremiumSnapshot premiumSnapshot, DomainEventPublisher eventPublisher)
            throws ReexecutionException {
        getPeriod().markAsInvalid();
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
        return getPeriod().createCopy();
    }

    protected Period getPeriod() {
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
                int registrationComparator = this.registration.compareTo(operation.registration);
                if (registrationComparator == 0) {
                    if (this == operation) return 0;
                    if (this.id == null) return 1;
                    if (operation.id == null) return -1;
                    return this.id.compareTo(operation.id);
                } else {
                    return registrationComparator;
                }
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
        return getPeriod().getMonthlyBalances(premiumSnapshot);
    }

    public LocalDateTime getRegistration() {
        return registration;
    }

    public abstract void handle(ReexecutionException e, DomainEventPublisher eventPublisher);

}