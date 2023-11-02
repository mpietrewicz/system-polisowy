package pl.mpietrewicz.sp.modules.balance.domain.balance.month;

import lombok.Getter;
import lombok.NoArgsConstructor;
import pl.mpietrewicz.sp.ddd.annotations.domain.ValueObject;
import pl.mpietrewicz.sp.modules.balance.ddd.support.domain.BaseEntity;
import pl.mpietrewicz.sp.modules.balance.domain.balance.AccountingMonth;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Inheritance;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import java.math.BigDecimal;
import java.util.List;

import static javax.persistence.InheritanceType.SINGLE_TABLE;

@ValueObject
@Entity
@Inheritance(strategy = SINGLE_TABLE)
@NoArgsConstructor
public abstract class MonthState extends BaseEntity {

    @OneToOne
    @JoinColumn(name = "month_id")
    protected Month month;

    @Getter
    @Enumerated(EnumType.STRING)
    private MonthStatus status;

    private BigDecimal underpayment = BigDecimal.ZERO;

    private BigDecimal overpayment = BigDecimal.ZERO;

    public MonthState(Month month, MonthStatus status, BigDecimal underpayment, BigDecimal overpayment) {
        this.month = month;
        this.status = status;
        this.underpayment = underpayment;
        this.overpayment = overpayment;
    }

    public abstract void pay(BigDecimal payment);

    public abstract void refund(BigDecimal refund);

    public abstract Month createNextMonth(AccountingMonth accountingMonth, List<ComponentPremium> componentPremiums); // todo: tutaj powinien być lust of component premiums

    public abstract BigDecimal getPaid();

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

    public Month getMonth() {
        return month;
    }

    public boolean isPaid() {
        return status.isPaid();
    }

    public boolean isNotPaid() {
        return status.isNotPaid();
    }

    public MonthStatus getStatus() {
        return status;
    }

    public BigDecimal getUnderpayment() {
        return underpayment;
    }

    public BigDecimal getOverpayment() {
        return overpayment;
    }
}