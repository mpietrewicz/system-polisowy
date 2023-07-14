package pl.mpietrewicz.sp.modules.oldbalance.domain.utils;

import pl.mpietrewicz.sp.modules.oldbalance.domain.balance.period.PeriodSnapshot;
import pl.mpietrewicz.sp.modules.oldbalance.domain.balance.period.PeriodStatus;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;

import static java.math.BigDecimal.ZERO;

public class PeriodSnapshotAssembler {

    private LocalDate month;
    private PeriodStatus status;
    private BigDecimal premiumDue;
    private BigDecimal underpayment = ZERO;
    private BigDecimal overpayment = ZERO;

    public PeriodSnapshotAssembler withMonth(String month) {
        this.month = YearMonth.parse(month).atDay(1);
        return this;
    }

    public PeriodSnapshotAssembler withStatus(PeriodStatus status) {
        this.status = status;
        return this;
    }

    public PeriodSnapshotAssembler withPremiumDue(int premiumDue) {
        this.premiumDue = new BigDecimal(premiumDue);
        return this;
    }

    public PeriodSnapshotAssembler withUnderpayment(int underpayment) {
        this.underpayment = new BigDecimal(underpayment);
        return this;
    }

    public PeriodSnapshotAssembler withOverpayment(int overpayment) {
        this.overpayment = new BigDecimal(overpayment);
        return this;
    }

    public PeriodSnapshot build() {
        return new PeriodSnapshot(month, status, premiumDue, underpayment, overpayment);
    }

}