package pl.mpietrewicz.sp.modules.oldbalance.domain.balance.policy.periodcover;

import pl.mpietrewicz.sp.modules.oldbalance.domain.balance.Periods;
import pl.mpietrewicz.sp.modules.oldbalance.domain.balance.operation.Operation;
import pl.mpietrewicz.sp.modules.oldbalance.domain.balance.operation.Refund;
import pl.mpietrewicz.sp.modules.oldbalance.domain.balance.period.Period;
import pl.mpietrewicz.sp.modules.oldbalance.domain.balance.policy.PeriodCoverPolicy;
import pl.mpietrewicz.sp.ddd.annotations.domain.ValueObject;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.Transient;
import java.time.YearMonth;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;

import static pl.mpietrewicz.sp.modules.oldbalance.domain.balance.period.Period.DESCENDING;

//@DomainPolicyImpl
@ValueObject
@Entity
@DiscriminatorValue("WITH_RENEWAL")
public class WithRenewal extends PeriodCoverPolicy {

    @Transient
    private static final int MAX_MONTHS_CONTIUNE = 9;

    @Transient
    private static final int MIN_MONTHS_TO_RENEWAL = 3;

    @Override
    public Period getFirstPeriodToPay(Periods periods, Operation operation) {
        Period periodAtPaymentMonth = periods.getAt(YearMonth.from(operation.getDate()))
                .orElseThrow(() -> new RuntimeException("No period at payment month!"));

        if (periodAtPaymentMonth.isPaid()) {
            List<Period> periodsSincePayment = periods.getSince(periodAtPaymentMonth);
            Optional<Period> firstNotPaidPeriod = getFirstNotPaidPeriod(periodsSincePayment);
            Optional<Period> lastPaidPeriod = getLastPaidPeriod(periodsSincePayment);

            return firstNotPaidPeriod.orElseGet(lastPaidPeriod::orElseThrow);
        } else if (periodAtPaymentMonth.isNotPaid()) {
            List<Period> periodsUntilPayment = periods.getUntil(periodAtPaymentMonth);
            Period lastCoveredPeriod = getLastCoveredPeriod(periodsUntilPayment)
                            .orElseGet(() -> periods.getFirstPeriod());
            long monthsBetween = ChronoUnit.MONTHS.between(lastCoveredPeriod.getMonth(), periodAtPaymentMonth.getMonth());

            if (monthsBetween > MAX_MONTHS_CONTIUNE) {
                throw new RuntimeException("Payment is after the extended period!");
            } else if (monthsBetween > MIN_MONTHS_TO_RENEWAL) {
                return periodAtPaymentMonth;
            } else {
                return lastCoveredPeriod;
            }
        } else {
            throw new IllegalStateException("Not supported state at finding first period to pay!");
        }
    }

    @Override
    public Period getLastPeriodToRefund(Periods periods, Refund refund) {
        return periods.getCovered().stream()
                .min(DESCENDING)
                .orElseThrow(() -> new RuntimeException("No periods to refund!"));
    }

    private Optional<Period> getFirstNotPaidPeriod(List<Period> periodsSincePaymentMonth) {
        return periodsSincePaymentMonth.stream()
                .filter(Period::isNotPaid)
                .min(Period.ASCENDING);
    }

    private Optional<Period> getLastPaidPeriod(List<Period> periodsSincePaymentMonth) {
        return periodsSincePaymentMonth.stream()
                .filter(Period::isPaid)
                .min(Period.ASCENDING);
    }

    private Optional<Period> getLastCoveredPeriod(List<Period> periodsUntilPaymentMonth) {
        return periodsUntilPaymentMonth.stream()
                .filter(Period::isCovered)
                .min(Period.DESCENDING);
    }

}