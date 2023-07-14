package pl.mpietrewicz.sp.modules.oldbalance.domain.balance.operation;

import lombok.Getter;
import lombok.Setter;
import pl.mpietrewicz.sp.modules.oldbalance.domain.balance.Operations;
import pl.mpietrewicz.sp.modules.oldbalance.domain.balance.Periods;
import pl.mpietrewicz.sp.modules.oldbalance.domain.balance.period.PeriodSnapshot;
import pl.mpietrewicz.sp.modules.oldbalance.domain.balance.policy.OperationCalculationPolicy;
import pl.mpietrewicz.sp.modules.oldbalance.domain.balance.policy.operationcalculation.PaymentCalculationPolicyEnum;
import pl.mpietrewicz.sp.modules.oldbalance.domain.balance.policy.operationcalculation.PaymentCalculationPolicyFactory;
import pl.mpietrewicz.sp.ddd.annotations.domain.ValueObject;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@ValueObject
@Entity
@DiscriminatorValue("PAYMENT")
public class Payment extends Operation {

    private LocalDate date;
    private final LocalDateTime registration = LocalDateTime.now();
    private BigDecimal amount;

    @OneToMany
    @JoinColumn(name = "payment_id")
    private List<PeriodSnapshot> periodSnapshots = new ArrayList<>();

    @Enumerated(EnumType.STRING)
    @Getter @Setter // todo: nie można tym sterować!
    private PaymentCalculationPolicyEnum paymentCalculationPolicyEnum;

    public Payment() {
    }

    public Payment(LocalDate date, BigDecimal amount, PaymentCalculationPolicyEnum paymentCalculationPolicyEnum) {
        this.date = date;
        this.amount = amount;
        this.paymentCalculationPolicyEnum = paymentCalculationPolicyEnum;
    }

    public Payment(LocalDate date, BigDecimal amount, List<PeriodSnapshot> periodSnapshots,
                   PaymentCalculationPolicyEnum paymentCalculationPolicyEnum) {
        this.date = date;
        this.amount = amount;
        this.periodSnapshots = periodSnapshots;
        this.type = OperationType.PAYMENT;
        this.paymentCalculationPolicyEnum = paymentCalculationPolicyEnum;
    }

    @Override
    public void allocate(Operations operations, Periods periods) {
        OperationCalculationPolicy operationCalculationPolicy = PaymentCalculationPolicyFactory.create(paymentCalculationPolicyEnum);
        operationCalculationPolicy.calculate(this, operations, periods);
    }

    @Override
    public void allocate(Operations operations, Periods periods, PaymentCalculationPolicyEnum paymentCalculationPolicyEnum) {
        OperationCalculationPolicy operationCalculationPolicy = PaymentCalculationPolicyFactory.create(paymentCalculationPolicyEnum);
        operationCalculationPolicy.calculate(this, operations, periods);
    }

    public Payment getSpecified() {
        return this;
    }

    public LocalDate getDate() {
        return date;
    }

    public LocalDateTime getRegistration() {
        return registration;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public PeriodSnapshot getLastPeriodSnapshot() {
        return periodSnapshots.stream()
                .max(Comparator.comparing(PeriodSnapshot::getMonth))
                .orElseThrow();
    }

    public boolean isCovering(YearMonth month) {
        return periodSnapshots.stream()
                .filter(periodSnapshot -> periodSnapshot.isAt(month))
                .anyMatch(PeriodSnapshot::isCovered);
    }

}