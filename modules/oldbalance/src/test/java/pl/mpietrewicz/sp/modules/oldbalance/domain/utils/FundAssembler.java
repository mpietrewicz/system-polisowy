package pl.mpietrewicz.sp.modules.oldbalance.domain.utils;

import pl.mpietrewicz.sp.modules.oldbalance.domain.balance.operation.Payment;
import pl.mpietrewicz.sp.modules.oldbalance.domain.balance.period.PeriodSnapshot;
import pl.mpietrewicz.sp.modules.oldbalance.domain.balance.policy.operationcalculation.PaymentCalculationPolicyEnum;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public class FundAssembler {

    private LocalDate date;
    private BigDecimal amount;
    private List<PeriodSnapshot> periodSnapshots;

    public FundAssembler withDate(String date) {
        this.date = LocalDate.parse(date);
        return this;
    }

    public FundAssembler withAmount(int amount) {
        this.amount = new BigDecimal(amount);
        return this;
    }

    public FundAssembler withPaidPeriods(List<PeriodSnapshot> periodSnapshots) {
        this.periodSnapshots = periodSnapshots;
        return this;
    }

    public Payment build() {
        return new Payment(date, amount, periodSnapshots, PaymentCalculationPolicyEnum.SIMPLE); // todo: czy tak to może pozostać?
    }

}