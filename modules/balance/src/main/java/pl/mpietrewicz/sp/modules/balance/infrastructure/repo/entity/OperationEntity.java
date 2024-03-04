package pl.mpietrewicz.sp.modules.balance.infrastructure.repo.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import pl.mpietrewicz.sp.ddd.canonicalmodel.publishedlanguage.AggregateId;
import pl.mpietrewicz.sp.ddd.canonicalmodel.publishedlanguage.PaymentPolicyEnum;
import pl.mpietrewicz.sp.ddd.support.infrastructure.repo.BaseEntity;
import pl.mpietrewicz.sp.modules.balance.domain.balance.operation.OperationType;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Table(name = "Operation")
@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class OperationEntity extends BaseEntity {

    protected LocalDate date;

    private LocalDateTime registration;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "operation_id")
    protected List<PeriodEntity> periods;

    @Enumerated(EnumType.STRING)
    protected OperationType type;

    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    private PaymentPolicyEnum paymentPolicyEnum;

    private AggregateId premiumId;

    private LocalDateTime timestamp;

    protected LocalDate end;

    public OperationEntity(Long entityId, LocalDate date, LocalDateTime registration, List<PeriodEntity> periods,
                           OperationType type, BigDecimal amount, PaymentPolicyEnum paymentPolicyEnum,
                           AggregateId premiumId, LocalDateTime timestamp, LocalDate end) {
        this.entityId = entityId;
        this.date = date;
        this.registration = registration;
        this.periods = periods;
        this.type = type;
        this.amount = amount;
        this.paymentPolicyEnum = paymentPolicyEnum;
        this.premiumId = premiumId;
        this.timestamp = timestamp;
        this.end = end;
    }

}