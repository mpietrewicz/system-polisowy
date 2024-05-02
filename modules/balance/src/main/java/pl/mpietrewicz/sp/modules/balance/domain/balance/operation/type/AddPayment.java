package pl.mpietrewicz.sp.modules.balance.domain.balance.operation.type;

import lombok.NoArgsConstructor;
import pl.mpietrewicz.sp.ddd.annotations.domain.ValueObject;
import pl.mpietrewicz.sp.ddd.canonicalmodel.events.AddPaymentFailedEvent;
import pl.mpietrewicz.sp.ddd.canonicalmodel.publishedlanguage.AggregateId;
import pl.mpietrewicz.sp.ddd.canonicalmodel.publishedlanguage.PaymentPolicyEnum;
import pl.mpietrewicz.sp.ddd.canonicalmodel.publishedlanguage.snapshot.premium.PremiumSnapshot;
import pl.mpietrewicz.sp.ddd.sharedkernel.valueobject.PositiveAmount;
import pl.mpietrewicz.sp.modules.balance.domain.balance.Balance;
import pl.mpietrewicz.sp.modules.balance.domain.balance.operation.Operation;
import pl.mpietrewicz.sp.modules.balance.domain.balance.operation.RequiredPeriod;
import pl.mpietrewicz.sp.modules.balance.domain.balance.period.Period;
import pl.mpietrewicz.sp.modules.balance.domain.balance.policy.payment.PaymentPolicy;
import pl.mpietrewicz.sp.modules.balance.domain.balance.policy.payment.PaymentPolicyFactory;
import pl.mpietrewicz.sp.modules.balance.exceptions.PaymentException;
import pl.mpietrewicz.sp.modules.balance.exceptions.ReexecutionException;

import javax.persistence.AttributeOverride;
import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.RollbackException;
import javax.persistence.Transient;
import java.time.LocalDate;
import java.time.LocalDateTime;

import static pl.mpietrewicz.sp.modules.balance.domain.balance.operation.OperationType.ADD_PAYMENT;
import static pl.mpietrewicz.sp.modules.balance.domain.balance.operation.RequiredPeriod.LAST_MONT;

@ValueObject
@Entity
@DiscriminatorValue("ADD_PAYMENT")
@NoArgsConstructor
public class AddPayment extends Operation {

    @Embedded
    @AttributeOverride(name = "aggregateId", column = @Column(name = "paymentId", nullable = false))
    private AggregateId paymentId;

    @Embedded
    @AttributeOverride(name = "value", column = @Column(name = "payment"))
    private PositiveAmount payment;

    @Enumerated(EnumType.STRING)
    private PaymentPolicyEnum paymentPolicyEnum;

    @Transient
    private static final RequiredPeriod requiredPeriod = LAST_MONT;

    public AddPayment(AggregateId paymentId, LocalDate date, PositiveAmount payment, PaymentPolicyEnum paymentPolicyEnum,
                      Balance balance) {
        super(date, LocalDateTime.now(), balance, ADD_PAYMENT);
        this.paymentId = paymentId;
        this.payment = payment;
        this.paymentPolicyEnum = paymentPolicyEnum;
    }

    @Override
    public void execute(Period period) {
        PremiumSnapshot premiumSnapshot = getPremiumSnapshot(registration);
        try {
            tryExecute(period, premiumSnapshot);
        } catch (PaymentException exception) {
            publishEvent(new AddPaymentFailedEvent(paymentId, date, payment, exception));
            throw new RollbackException(exception);
        }
    }

    @Override
    protected void reexecute(Period period, LocalDateTime registration) throws ReexecutionException {
        PremiumSnapshot premiumSnapshot = getPremiumSnapshot(registration);
        try {
            tryExecute(period, premiumSnapshot);
        } catch (PaymentException exception) {
            throw new ReexecutionException(exception, "Failed add payment {} during reexecution!", paymentId.getId());
        }
    }

    @Override
    public RequiredPeriod getRequiredPeriod() {
        return requiredPeriod;
    }

    @Override
    public void publishFailedEvent(ReexecutionException exception) {
        publishEvent(new AddPaymentFailedEvent(paymentId, date, payment, exception));
    }

    private void tryExecute(Period period, PremiumSnapshot premiumSnapshot) throws PaymentException {
        PaymentPolicy paymentPolicy = PaymentPolicyFactory.create(paymentPolicyEnum, premiumSnapshot);
        paymentPolicy.pay(period, date, payment);
    }

}