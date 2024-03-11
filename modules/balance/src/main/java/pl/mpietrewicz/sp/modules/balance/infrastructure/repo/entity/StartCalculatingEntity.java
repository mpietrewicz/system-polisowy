package pl.mpietrewicz.sp.modules.balance.infrastructure.repo.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import pl.mpietrewicz.sp.modules.balance.domain.balance.operation.OperationType;

import javax.persistence.Entity;
import javax.persistence.Table;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Table(name = "start_calculating")
@Entity
@Getter
@NoArgsConstructor
public class StartCalculatingEntity extends OperationEntity {

    private BigDecimal premium;

    public StartCalculatingEntity(Long entityId, LocalDateTime registration, OperationType type, LocalDate date,
                                  List<PeriodEntity> periods, BigDecimal premium) {
        this.entityId = entityId;
        this.registration = registration;
        this.type = type;
        this.date = date;
        this.periods = periods;
        this.premium = premium;
    }

}