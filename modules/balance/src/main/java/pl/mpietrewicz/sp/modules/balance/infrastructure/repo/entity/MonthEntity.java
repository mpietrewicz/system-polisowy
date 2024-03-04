package pl.mpietrewicz.sp.modules.balance.infrastructure.repo.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import pl.mpietrewicz.sp.ddd.support.infrastructure.repo.BaseEntity;
import pl.mpietrewicz.sp.modules.balance.domain.balance.month.PaidStatus;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Table;
import java.math.BigDecimal;
import java.time.YearMonth;

@Table(name = "Month")
@Entity
@Getter
@NoArgsConstructor
public class MonthEntity extends BaseEntity {

    private YearMonth yearMonth;

    protected BigDecimal premium;

    @Enumerated(EnumType.STRING)
    public PaidStatus paidStatus;

    public BigDecimal paid;

    public boolean isRenewal;

    public MonthEntity(Long entityId, YearMonth yearMonth, BigDecimal premium, PaidStatus paidStatus,
                       BigDecimal paid, boolean isRenewal) {
        this.entityId = entityId;
        this.yearMonth = yearMonth;
        this.premium = premium;
        this.paidStatus = paidStatus;
        this.paid = paid;
        this.isRenewal = isRenewal;
    }

}