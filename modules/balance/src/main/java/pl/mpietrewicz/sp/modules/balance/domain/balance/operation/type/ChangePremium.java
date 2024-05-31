package pl.mpietrewicz.sp.modules.balance.domain.balance.operation.type;

import lombok.NoArgsConstructor;
import pl.mpietrewicz.sp.ddd.annotations.domain.ValueObject;
import pl.mpietrewicz.sp.ddd.canonicalmodel.events.ChangePremiumFailedEvent;
import pl.mpietrewicz.sp.ddd.canonicalmodel.publishedlanguage.snapshot.premium.PremiumSnapshot;
import pl.mpietrewicz.sp.ddd.sharedkernel.valueobject.Amount;
import pl.mpietrewicz.sp.ddd.sharedkernel.valueobject.PositiveAmount;
import pl.mpietrewicz.sp.modules.balance.domain.balance.Balance;
import pl.mpietrewicz.sp.modules.balance.domain.balance.operation.Operation;
import pl.mpietrewicz.sp.modules.balance.domain.balance.operation.RequiredPeriod;
import pl.mpietrewicz.sp.modules.balance.domain.balance.period.Period;
import pl.mpietrewicz.sp.modules.balance.domain.balance.policy.payment.PaymentPolicy;
import pl.mpietrewicz.sp.modules.balance.domain.balance.policy.payment.PaymentPolicyFactory;
import pl.mpietrewicz.sp.modules.balance.exceptions.ChangePremiumException;
import pl.mpietrewicz.sp.modules.balance.exceptions.PaymentException;
import pl.mpietrewicz.sp.modules.balance.exceptions.ReexecutionException;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.RollbackException;
import javax.persistence.Transient;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;

import static pl.mpietrewicz.sp.ddd.canonicalmodel.publishedlanguage.PaymentPolicyEnum.CONTINUATION;
import static pl.mpietrewicz.sp.modules.balance.domain.balance.operation.OperationType.CHANGE_PREMIUM;
import static pl.mpietrewicz.sp.modules.balance.domain.balance.operation.RequiredPeriod.MONTHS_AFTER;

@ValueObject
@Entity
@DiscriminatorValue("CHANGE_PREMIUM")
@NoArgsConstructor
public class ChangePremium extends Operation {

    @Transient
    private static final RequiredPeriod requiredPeriod = MONTHS_AFTER;

    public ChangePremium(LocalDate date, LocalDateTime timestamp, Balance balance) {
        super(date, timestamp, balance, CHANGE_PREMIUM);
    }

    @Override
    public void execute(Period period) {
        PremiumSnapshot premiumSnapshot = getPremiumSnapshot(registration);
        try {
            tryExecute(period, premiumSnapshot);
        } catch (PaymentException exception) {
            ChangePremiumException changePremiumException = new ChangePremiumException(premiumSnapshot, exception,
                    "Failed change premium!");
            publishEvent(new ChangePremiumFailedEvent(premiumSnapshot, changePremiumException));
            throw new RollbackException(exception);
        }
    }

    @Override
    protected void reexecute(Period period, LocalDateTime registration) throws ReexecutionException {
        PremiumSnapshot premiumSnapshot = getPremiumSnapshot(registration);
        try {
            tryExecute(period, premiumSnapshot);
        } catch (PaymentException exception) {
            throw new ReexecutionException(exception, "Failed change premium {} during reexecution!",
                    premiumSnapshot.getPremiumId().getId());
        }
    }

    @Override
    public RequiredPeriod getRequiredPeriod() {
        return requiredPeriod;
    }

    @Override
    public void publishFailedEvent(ReexecutionException exception) {
        throw new UnsupportedOperationException();
    }

    private void tryExecute(Period period, PremiumSnapshot premiumSnapshot) throws PaymentException {
        YearMonth monthOfChange = YearMonth.from(date);
        Amount refunded = period.refundUpTo(monthOfChange);

        if (refunded.isPositive()) {
            PaymentPolicy paymentPolicy = PaymentPolicyFactory.create(CONTINUATION, premiumSnapshot);
            paymentPolicy.pay(period, date, (PositiveAmount) refunded);
        }
    }

}