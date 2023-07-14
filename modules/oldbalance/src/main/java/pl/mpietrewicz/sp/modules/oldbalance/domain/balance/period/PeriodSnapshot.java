package pl.mpietrewicz.sp.modules.oldbalance.domain.balance.period;

import pl.mpietrewicz.sp.ddd.annotations.domain.ValueObject;
import pl.mpietrewicz.sp.ddd.support.domain.BaseEntity;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;

@ValueObject
@Entity
public class PeriodSnapshot extends BaseEntity {

    private LocalDate month;

    @Enumerated(EnumType.STRING)
    private PeriodStatus status;

    private BigDecimal premiumDue;

    private BigDecimal underpayment;
    private BigDecimal overpayment;

    private Long changeId; // todo: w przyszłości zapisywać co spowodowalo tę zmianę (wpłata, czy zmiana składki po DSK / PSU)

    public PeriodSnapshot() {
    }

    public PeriodSnapshot(LocalDate month, PeriodStatus status, BigDecimal premiumDue,
                          BigDecimal underpayment, BigDecimal overpayment) {
        this.month = month;
        this.status = status;
        this.premiumDue = premiumDue;
        this.underpayment = underpayment;
        this.overpayment = overpayment;
    }

    public YearMonth getMonth() {
        return YearMonth.from(month);
    }

    public PeriodStatus getStatus() {
        return status;
    }

    public BigDecimal getUnderpayment() {
        return underpayment;
    }

    public BigDecimal getOverpayment() {
        return overpayment;
    }

    public boolean isAt(YearMonth month) {
        return YearMonth.from(this.month).compareTo(month) == 0;
    }

    public boolean isCovered() {
        return status.isCovered();
    }

}