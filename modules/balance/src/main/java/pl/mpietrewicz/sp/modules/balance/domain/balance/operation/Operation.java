package pl.mpietrewicz.sp.modules.balance.domain.balance.operation;

import lombok.Getter;
import pl.mpietrewicz.sp.ddd.canonicalmodel.publishedlanguage.snapshot.premium.PremiumSnapshot;
import pl.mpietrewicz.sp.ddd.support.domain.DomainEventPublisher;
import pl.mpietrewicz.sp.modules.balance.domain.balance.Period;
import pl.mpietrewicz.sp.modules.balance.domain.balance.operation.type.CancelStopCalculating;
import pl.mpietrewicz.sp.modules.balance.domain.balance.operation.type.StartCalculating;
import pl.mpietrewicz.sp.modules.balance.domain.balance.operation.type.StopCalculating;
import pl.mpietrewicz.sp.modules.balance.exceptions.BalanceException;
import pl.mpietrewicz.sp.modules.balance.exceptions.ReexecutionException;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.RollbackException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Getter
public abstract class Operation {

    private Long id;

    protected final LocalDate date;

    private final LocalDateTime registration;

    protected final List<Period> periods;

    @Enumerated(EnumType.STRING)
    protected OperationType type;

    protected Operation(LocalDate date) {
        this.registration = LocalDateTime.now();
        this.date = date;
        this.periods = new ArrayList<>();
    }

    protected Operation(LocalDateTime registration) {
        this.registration = registration;
        this.date = registration.toLocalDate();
        this.periods = new ArrayList<>();
    }

    protected Operation(Long id, LocalDate date, LocalDateTime registration, List<Period> periods) {
        this.id = id;
        this.date = date;
        this.registration = registration;
        this.periods = periods;
    }

    public void execute(Operation previousOperation, PremiumSnapshot premiumSnapshot, DomainEventPublisher eventPublisher) {
        periods.add(previousOperation.getPeriodCopy());
        execute(premiumSnapshot, eventPublisher);
    }

    public void reexecute(Operation previousOperation, PremiumSnapshot premiumSnapshot, DomainEventPublisher eventPublisher)
            throws ReexecutionException {
        getPeriod().markAsInvalid();
        periods.add(previousOperation.getPeriodCopy());
        reexecute(premiumSnapshot, eventPublisher);
    }

    public void handle(BalanceException e, DomainEventPublisher eventPublisher) {
        publishFailedEvent(e, eventPublisher);
        throw new RollbackException(e);
    }

    protected abstract void execute(PremiumSnapshot premiumSnapshot, DomainEventPublisher eventPublisher);

    protected abstract void reexecute(PremiumSnapshot premiumSnapshot, DomainEventPublisher eventPublisher)
            throws ReexecutionException;

    protected abstract void publishFailedEvent(Exception e, DomainEventPublisher eventPublisher);

    public Period getPeriodCopy() {
        return getPeriod().createCopy();
    }

    public Period getPeriod() {
        return periods.stream()
                .filter(Period::isValid)
                .findFirst()
                .orElseThrow();
    }

    public int orderComparator(Operation operation) {
        if (operation instanceof StartCalculating
                || operation instanceof StopCalculating
                || operation instanceof CancelStopCalculating) {
            return - operation.orderComparator(this);
        }

        int dateComparator = date.compareTo(operation.date);
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

    protected Integer getPriority() { // descending
        return 10;
    }

    public boolean isAfter(Operation operation) {
        return orderComparator(operation) > 0;
    }

    public boolean isAfter(Optional<Operation> operation) {
        return operation.isPresent() && orderComparator(operation.get()) > 0;
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

    public LocalDateTime getRegistration() {
        return registration;
    }

    public boolean isValid() {
        return true;
    }

}