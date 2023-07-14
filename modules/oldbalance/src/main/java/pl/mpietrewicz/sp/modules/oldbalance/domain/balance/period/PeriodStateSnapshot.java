package pl.mpietrewicz.sp.modules.oldbalance.domain.balance.period;

import pl.mpietrewicz.sp.ddd.annotations.domain.ValueObject;
import pl.mpietrewicz.sp.ddd.support.domain.BaseEntity;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import java.math.BigDecimal;
import java.time.LocalDate;

@ValueObject
@Entity
public class PeriodStateSnapshot extends BaseEntity {

    private LocalDate month;

    @Enumerated(EnumType.STRING)
    private PeriodStatus status;
    private BigDecimal underpayment;
    private BigDecimal overpayment;

    private BigDecimal premiumDue;

    private Long changeId; // todo: w przyszłości zapisywać co spowodowalo tę zmianę (wpłata, czy zmiana składki po DSK / PSU)

    public PeriodStateSnapshot() {
    }

    public PeriodStateSnapshot(LocalDate month, PeriodStatus status, BigDecimal premiumDue,
                               BigDecimal underpayment, BigDecimal overpayment) {
        this.month = month;
        this.status = status;
        this.premiumDue = premiumDue;
        this.underpayment = underpayment;
        this.overpayment = overpayment;
    }

}