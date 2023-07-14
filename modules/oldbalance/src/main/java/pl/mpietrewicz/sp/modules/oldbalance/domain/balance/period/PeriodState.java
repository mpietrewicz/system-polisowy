package pl.mpietrewicz.sp.modules.oldbalance.domain.balance.period;

import lombok.Getter;
import lombok.NoArgsConstructor;
import pl.mpietrewicz.sp.ddd.annotations.domain.ValueObject;
import pl.mpietrewicz.sp.ddd.support.domain.BaseEntity;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Inheritance;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import java.math.BigDecimal;

import static javax.persistence.InheritanceType.SINGLE_TABLE;

@ValueObject
@Entity
@Inheritance(strategy = SINGLE_TABLE)
@NoArgsConstructor
public abstract class PeriodState extends BaseEntity {

    @OneToOne
    @JoinColumn(name = "period_id")
    protected Period period;

    @Getter
    @Enumerated(EnumType.STRING)
    private PeriodStatus status;

    @Getter
    private BigDecimal underpayment = BigDecimal.ZERO;

    @Getter
    private BigDecimal overpayment = BigDecimal.ZERO;

    public PeriodState(Period period, PeriodStatus status, BigDecimal underpayment, BigDecimal overpayment) {
        this.period = period;
        this.status = status;
        this.underpayment = underpayment;
        this.overpayment = overpayment;
    }

    public void increaseUnderpayment(BigDecimal amount) {
        this.underpayment = this.underpayment.add(amount);
    }

    public void decreaseUnderpayment(BigDecimal amount) {
        this.underpayment = this.underpayment.subtract(amount);
    }

    public void increaseOverpayment(BigDecimal amount) {
        this.overpayment = this.overpayment.add(amount);
    }

    public void decreaseOverpayment(BigDecimal amount) {
        this.overpayment = this.overpayment.subtract(amount);
    }

    public abstract void pay(BigDecimal payment);

    public abstract void refund(BigDecimal refund);

    public abstract Period createNextPeriod();

}