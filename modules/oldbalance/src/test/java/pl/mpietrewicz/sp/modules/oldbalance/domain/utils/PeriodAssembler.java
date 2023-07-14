package pl.mpietrewicz.sp.modules.oldbalance.domain.utils;

import pl.mpietrewicz.sp.modules.oldbalance.domain.balance.PremiumDue;
import pl.mpietrewicz.sp.modules.oldbalance.domain.balance.period.Period;
import pl.mpietrewicz.sp.modules.oldbalance.domain.balance.period.PeriodState;
import pl.mpietrewicz.sp.modules.oldbalance.domain.balance.period.PeriodStatus;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import java.math.BigDecimal;
import java.time.YearMonth;

public class PeriodAssembler {

    private PremiumDue premiumDue;
    private YearMonth month;
    @Enumerated(EnumType.STRING)
    private PeriodStatus status;
    private BigDecimal underpayment = BigDecimal.ZERO;
    private BigDecimal overpayment = BigDecimal.ZERO;
    private PeriodState periodState;

    public PeriodAssembler withDue(PremiumDue premiumDue) {
        this.premiumDue = premiumDue;
        return this;
    }

    public PeriodAssembler withMonth(String month) {
        this.month = YearMonth.parse(month);
        return this;
    }

    public PeriodAssembler withStatus(PeriodStatus status) {
        this.status = status;
        return this;
    }

    public PeriodAssembler withUnderpayment(int underpayment) {
        this.underpayment = new BigDecimal(underpayment);
        return this;
    }

    public PeriodAssembler withOverpayment(int overpayment) {
        this.overpayment = new BigDecimal(overpayment);
        return this;
    }

    public PeriodAssembler withPeriodState(PeriodState periodState) {
        this.periodState = periodState;
        return this;
    }

    public Period build() {
        return new Period(month, premiumDue, status, underpayment, overpayment);
    }
}