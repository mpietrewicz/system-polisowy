package pl.mpietrewicz.sp.modules.balance.domain.balance.operation;

import lombok.Getter;
import pl.mpietrewicz.sp.ddd.canonicalmodel.publishedlanguage.AggregateId;
import pl.mpietrewicz.sp.ddd.support.domain.DomainEventPublisher;
import pl.mpietrewicz.sp.modules.balance.domain.balance.Period;
import pl.mpietrewicz.sp.modules.balance.domain.balance.operation.type.CancelStopCalculating;
import pl.mpietrewicz.sp.modules.balance.domain.balance.operation.type.StartCalculating;
import pl.mpietrewicz.sp.modules.balance.domain.balance.operation.type.StopCalculating;
import pl.mpietrewicz.sp.modules.balance.exceptions.BalanceException;
import pl.mpietrewicz.sp.modules.balance.exceptions.ReexecutionException;

import javax.inject.Inject;
import javax.persistence.RollbackException;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;

@Getter
public abstract class Operation {

    @Inject
    protected DomainEventPublisher eventPublisher;

    private Long id;

    protected final LocalDate date;

    private final LocalDateTime registration;

    protected final List<Period> periods;

    protected Operation(LocalDate date, DomainEventPublisher eventPublisher) {
        this.registration = LocalDateTime.now();
        this.date = date;
        this.eventPublisher = eventPublisher;
        this.periods = new ArrayList<>();
    }

    protected Operation(LocalDateTime registration, DomainEventPublisher eventPublisher) {
        this.registration = registration;
        this.date = registration.toLocalDate();
        this.eventPublisher = eventPublisher;
        this.periods = new ArrayList<>();
    }

    protected Operation(LocalDate date, LocalDateTime registration, DomainEventPublisher eventPublisher) {
        this.date = date;
        this.registration = registration;
        this.eventPublisher = eventPublisher;
        this.periods = new ArrayList<>();
    }

    protected Operation(Long id, LocalDate date, LocalDateTime registration, List<Period> periods) {
        this.id = id;
        this.date = date;
        this.registration = registration;
        this.periods = periods;
    }

    public void execute(Operation previousOperation, AggregateId contractId) {
        periods.add(previousOperation.getPeriodCopy("execute"));
        execute(contractId);
    }

    public void reexecute(Operation previousOperation, AggregateId contractId, LocalDateTime registration, String info)
            throws ReexecutionException {
        getPeriod().markAsInvalid();
        periods.add(previousOperation.getPeriodCopy(info));
        reexecute(contractId, registration);
    }

    public void handle(BalanceException e) {
        publishFailedEvent(e);
        throw new RollbackException(e);
    }

    protected abstract void execute(AggregateId contractId);

    protected abstract void reexecute(AggregateId contractId, LocalDateTime registration) throws ReexecutionException;

    protected abstract void publishFailedEvent(Exception e);

    protected abstract OperationType getOperationType();

    public Period getPeriodCopy(String info) {
        return getPeriod().createCopy(info);
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

        if (dateComparator(operation) != 0) return dateComparator(operation);
        if (priorityComparator(operation) != 0) return priorityComparator(operation);
        if (registrationComparator(operation) != 0) return registrationComparator(operation);
        if (idComparator(operation) != 0) return idComparator(operation);
        if (this == operation) return 0;
        throw new IllegalStateException("Operations cannot be uniquely sorted");
    }

    protected Integer getPriority() { // descending
        return 10;
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

    public LocalDateTime getRegistration() {
        return registration;
    }

    public boolean isValid() {
        return true;
    }

    public void publish(Serializable event) {
        eventPublisher.publish(event, "BalanceServiceImpl");
    }

    private int dateComparator(Operation operation) {
        return date.compareTo(operation.date);
    }

    private int priorityComparator(Operation operation) {
        return this.getPriority().compareTo(operation.getPriority());
    }

    private int registrationComparator(Operation operation) {
        return this.registration.compareTo(operation.registration);
    }

    private int idComparator(Operation operation) {
        if (this.id == null) return 1;
        if (operation.id == null) return -1;
        return this.id.compareTo(operation.id);
    }

}