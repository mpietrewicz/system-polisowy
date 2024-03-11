package pl.mpietrewicz.sp.modules.balance.infrastructure.repo.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import pl.mpietrewicz.sp.ddd.support.infrastructure.repo.BaseEntity;
import pl.mpietrewicz.sp.modules.balance.domain.balance.operation.OperationType;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Table(name = "operation")
@Entity
@Getter
@NoArgsConstructor
@Inheritance(strategy = InheritanceType.JOINED)
public abstract class OperationEntity extends BaseEntity {

    protected LocalDateTime registration;

    @Enumerated(EnumType.STRING)
    protected OperationType type;

    protected LocalDate date;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "operation_id")
    protected List<PeriodEntity> periods;

}