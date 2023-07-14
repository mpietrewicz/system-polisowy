package pl.mpietrewicz.sp.modules.oldbalance.domain.balance.operation;

import lombok.Getter;
import lombok.NoArgsConstructor;
import pl.mpietrewicz.sp.modules.oldbalance.domain.balance.Operations;
import pl.mpietrewicz.sp.modules.oldbalance.domain.balance.Periods;
import pl.mpietrewicz.sp.modules.oldbalance.domain.balance.policy.operationcalculation.PaymentCalculationPolicyEnum;
import pl.mpietrewicz.sp.ddd.annotations.domain.ValueObject;
import pl.mpietrewicz.sp.ddd.support.domain.BaseEntity;

import javax.persistence.DiscriminatorColumn;
import javax.persistence.DiscriminatorType;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Comparator;

import static pl.mpietrewicz.sp.modules.oldbalance.domain.balance.operation.OperationType.PAYMENT;

@ValueObject
@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "operation_type", discriminatorType = DiscriminatorType.STRING)
@NoArgsConstructor
public abstract class Operation extends BaseEntity {

    @Getter
    private LocalDate date;

    @Getter
    private final LocalDateTime registration = LocalDateTime.now();

    @Getter
    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    protected OperationType type;

    public abstract void allocate(Operations operations, Periods periods);

    public abstract void allocate(Operations operations, Periods periods, PaymentCalculationPolicyEnum paymentCalculationPolicyEnum);

    public boolean isPayment() {
        return type == PAYMENT;
    }

    public boolean isRefund() {
        return type == OperationType.REFUND;
    }

    public int compareDate(Operation operation) {
        return dateComparator().compare(this, operation);
    }

    public boolean isBefore(Operation operation) {
        return compareDate(operation) < 0;
    }

    public boolean isAfter(Operation operation) {
        return compareDate(operation) > 0;
    }

    public Comparator<Operation> dateComparator() {
        return Comparator.comparing(Operation::getDate)
                .thenComparing(Operation::getRegistration);
    }



}