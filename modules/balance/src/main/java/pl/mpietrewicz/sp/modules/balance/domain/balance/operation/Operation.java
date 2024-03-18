package pl.mpietrewicz.sp.modules.balance.domain.balance.operation;

import lombok.NoArgsConstructor;
import pl.mpietrewicz.sp.ddd.canonicalmodel.publishedlanguage.snapshot.premium.PremiumSnapshot;
import pl.mpietrewicz.sp.ddd.support.infrastructure.repo.BaseEntity;
import pl.mpietrewicz.sp.modules.balance.domain.balance.Balance;
import pl.mpietrewicz.sp.modules.balance.domain.balance.Period;
import pl.mpietrewicz.sp.modules.balance.domain.balance.PeriodProvider;
import pl.mpietrewicz.sp.modules.balance.domain.balance.operation.type.CancelStopCalculating;
import pl.mpietrewicz.sp.modules.balance.domain.balance.operation.type.StartCalculating;
import pl.mpietrewicz.sp.modules.balance.domain.balance.operation.type.StopCalculating;
import pl.mpietrewicz.sp.modules.balance.exceptions.ReexecutionException;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@NoArgsConstructor
@Inheritance(strategy = InheritanceType.JOINED)
public abstract class Operation extends BaseEntity {

    protected LocalDate date;

    protected LocalDateTime registration;

    @Enumerated(EnumType.STRING)
    private OperationType operationType;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "operation_id")
    protected List<Period> periods;

    @ManyToOne(cascade = CascadeType.PERSIST)
    protected Balance balance;

    protected Operation(LocalDateTime registration, Balance balance, OperationType operationType) {
        this.registration = registration;
        this.date = registration.toLocalDate();
        this.balance = balance;
        this.periods = new ArrayList<>();
        this.operationType = operationType;
    }

    protected Operation(LocalDate date, LocalDateTime registration, Balance balance, OperationType operationType) {
        this.date = date;
        this.registration = registration;
        this.balance = balance;
        this.periods = new ArrayList<>();
        this.operationType = operationType;
    }

    public void execute(PeriodProvider previousPeriod) {
        periods.add(previousPeriod.getCopy("execute"));
        execute();
    }

    public void reexecute(PeriodProvider previousPeriod, Operation initialOperation) throws ReexecutionException {
        getPeriod().markAsInvalid();
        String info = "reexecute after " + initialOperation.operationType + " : " + initialOperation.date;
        periods.add(previousPeriod.getCopy(info));
        reexecute(initialOperation.registration);
    }

    public abstract void publishFailedEvent(ReexecutionException exception);

    public Period getPeriod() {
        return periods.stream()
                .filter(Period::isValid)
                .findFirst()
                .orElseThrow();
    }

    public int orderComparator(Operation operation) {
        if (this == operation) return 0;

        if (operation instanceof StartCalculating
                || operation instanceof StopCalculating
                || operation instanceof CancelStopCalculating) {
            return - operation.orderComparator(this);
        }

        if (dateComparator(operation) != 0) return dateComparator(operation);
        if (priorityComparator(operation) != 0) return priorityComparator(operation);
        if (registrationComparator(operation) != 0) return registrationComparator(operation);
        if (idComparator(operation) != 0) return idComparator(operation);
        throw new IllegalStateException("Operations cannot be uniquely sorted");
    }

    public boolean isValid() {
        return true;
    }

    public boolean isAfter(Operation operation) {
        return orderComparator(operation) > 0;
    }

    public boolean isBefore(Operation operation) {
        return orderComparator(operation) < 0;
    }

    protected abstract void execute();

    protected abstract void reexecute(LocalDateTime registration) throws ReexecutionException;

    protected Integer getPriority() { // descending
        return 10;
    }

    protected PremiumSnapshot getPremiumSnapshot(LocalDateTime timestamp) {
        return balance.getPremiumSnapshot(timestamp);
    }

    protected void publishEvent(Serializable event) {
        balance.publishEvent(event);
    }

    private int dateComparator(Operation operation) {
        return date.compareTo(operation.date);
    }

    private int priorityComparator(Operation operation) {
        return getPriority().compareTo(operation.getPriority());
    }

    private int registrationComparator(Operation operation) {
        return registration.compareTo(operation.registration);
    }

    private int idComparator(Operation operation) {
        if (entityId == null) return 1;
        if (operation.entityId == null) return -1;
        return entityId.compareTo(operation.entityId);
    }

}