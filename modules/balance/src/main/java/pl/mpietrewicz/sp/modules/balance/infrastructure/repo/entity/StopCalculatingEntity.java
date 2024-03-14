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

@Table(name = "stop_calculating")
@Entity
@Getter
@NoArgsConstructor
public class StopCalculatingEntity extends OperationEntity {

    private BigDecimal excess;

    private LocalDate end;

    private boolean valid;

    public StopCalculatingEntity(Long entityId, LocalDateTime registration, OperationType type, LocalDate date,
                               List<PeriodEntity> periods, BigDecimal excess, LocalDate end, boolean valid) {
        this.entityId = entityId;
        this.registration = registration;
        this.type = type;
        this.date = date;
        this.periods = periods;
        this.excess = excess;
        this.end = end;
        this.valid = valid;
    }

}