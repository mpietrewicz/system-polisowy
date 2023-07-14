package pl.mpietrewicz.sp.modules.oldbalance.domain.balance.operation;

import pl.mpietrewicz.sp.modules.oldbalance.domain.balance.Operations;
import pl.mpietrewicz.sp.modules.oldbalance.domain.balance.Periods;
import pl.mpietrewicz.sp.modules.oldbalance.domain.balance.policy.OperationCalculationPolicy;
import pl.mpietrewicz.sp.modules.oldbalance.domain.balance.policy.operationcalculation.PaymentCalculationPolicyEnum;
import pl.mpietrewicz.sp.modules.oldbalance.domain.balance.policy.operationcalculation.PaymentCalculationPolicyFactory;
import pl.mpietrewicz.sp.ddd.annotations.domain.ValueObject;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@ValueObject
@Entity
@DiscriminatorValue("REFUND")
public class Refund extends Operation {

    private LocalDate date;
    private final LocalDateTime registration = LocalDateTime.now();
    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    private PaymentCalculationPolicyEnum paymentCalculationPolicyEnum;

    public Refund() {
    }

    public Refund(LocalDate date, BigDecimal amount, PaymentCalculationPolicyEnum paymentCalculationPolicyEnum) {
        this.date = date;
        this.amount = amount;
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

    public Refund getSpecified() {
        return this;
    }

    public LocalDate getDate() {
        return date;
    }

    public BigDecimal getAmount() {
        return amount;
    }

}