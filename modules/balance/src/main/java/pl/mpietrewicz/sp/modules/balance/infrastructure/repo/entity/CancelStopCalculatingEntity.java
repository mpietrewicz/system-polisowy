package pl.mpietrewicz.sp.modules.balance.infrastructure.repo.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import pl.mpietrewicz.sp.modules.balance.domain.balance.operation.OperationType;

import javax.persistence.Entity;
import javax.persistence.Table;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Table(name = "cancel_stop_calculating")
@Entity
@Getter
@NoArgsConstructor
public class CancelStopCalculatingEntity extends OperationEntity {

    private LocalDate canceledEnd;

    private boolean valid;

    public CancelStopCalculatingEntity(Long entityId, LocalDateTime registration, OperationType type, LocalDate date,
                                       List<PeriodEntity> periods, LocalDate canceledEnd, boolean valid) {
        this.entityId = entityId;
        this.registration = registration;
        this.type = type;
        this.date = date;
        this.periods = periods;
        this.canceledEnd = canceledEnd;
        this.valid = valid;
    }

}