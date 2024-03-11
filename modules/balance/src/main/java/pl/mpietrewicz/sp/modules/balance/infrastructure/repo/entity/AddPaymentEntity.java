package pl.mpietrewicz.sp.modules.balance.infrastructure.repo.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import pl.mpietrewicz.sp.ddd.canonicalmodel.publishedlanguage.PaymentPolicyEnum;
import pl.mpietrewicz.sp.modules.balance.domain.balance.operation.OperationType;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Table;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Table(name = "add_payment")
@Entity
@Getter
@NoArgsConstructor
public class AddPaymentEntity extends OperationEntity {

    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    private PaymentPolicyEnum paymentPolicyEnum;

    public AddPaymentEntity(Long entityId, LocalDateTime registration, OperationType type, LocalDate date,
                            List<PeriodEntity> periods, BigDecimal amount, PaymentPolicyEnum paymentPolicyEnum) {
        this.entityId = entityId;
        this.registration = registration;
        this.type = type;
        this.date = date;
        this.periods = periods;
        this.amount = amount;
        this.paymentPolicyEnum = paymentPolicyEnum;
    }

}