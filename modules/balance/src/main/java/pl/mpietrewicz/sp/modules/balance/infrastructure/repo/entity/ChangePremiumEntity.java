package pl.mpietrewicz.sp.modules.balance.infrastructure.repo.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import pl.mpietrewicz.sp.modules.balance.domain.balance.operation.OperationType;

import javax.persistence.Entity;
import javax.persistence.Table;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Table(name = "change_premium")
@Entity
@Getter
@NoArgsConstructor
public class ChangePremiumEntity extends OperationEntity {


    public ChangePremiumEntity(Long entityId, LocalDateTime registration, OperationType type, LocalDate date,
                               List<PeriodEntity> periods) {
        this.entityId = entityId;
        this.registration = registration;
        this.type = type;
        this.date = date;
        this.periods = periods;
    }

}