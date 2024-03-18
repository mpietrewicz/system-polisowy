package pl.mpietrewicz.sp.modules.balance.domain.balance.operation.type;

import lombok.NoArgsConstructor;
import pl.mpietrewicz.sp.ddd.annotations.domain.ValueObject;
import pl.mpietrewicz.sp.ddd.canonicalmodel.events.AddPaymentFailedEvent;
import pl.mpietrewicz.sp.ddd.canonicalmodel.publishedlanguage.AggregateId;
import pl.mpietrewicz.sp.ddd.canonicalmodel.publishedlanguage.PaymentPolicyEnum;
import pl.mpietrewicz.sp.ddd.canonicalmodel.publishedlanguage.snapshot.premium.PremiumSnapshot;
import pl.mpietrewicz.sp.ddd.sharedkernel.Amount;
import pl.mpietrewicz.sp.modules.balance.domain.balance.Balance;
import pl.mpietrewicz.sp.modules.balance.domain.balance.operation.Operation;
import pl.mpietrewicz.sp.modules.balance.domain.balance.Period;
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
import java.time.LocalDate;
import java.time.LocalDateTime;

import static pl.mpietrewicz.sp.modules.balance.domain.balance.operation.OperationType.ADD_PAYMENT;

@ValueObject
@Entity
@DiscriminatorValue("ADD_PAYMENT")
@NoArgsConstructor
public class AddPayment extends Operation {

    @Embedded
    @AttributeOverride(name = "aggregateId", column = @Column(name = "paymentId", nullable = false))
    private AggregateId paymentId;

    @Embedded
    @AttributeOverride(name = "value", column = @Column(name = "amount"))
    private Amount amount;

    @Enumerated(EnumType.STRING)
    private PaymentPolicyEnum paymentPolicyEnum;

    public AddPayment(AggregateId paymentId, LocalDate date, Amount amount, PaymentPolicyEnum paymentPolicyEnum,
                      Balance balance) {
        super(date, LocalDateTime.now(), balance, ADD_PAYMENT);
        this.paymentId = paymentId;
        this.amount = amount;
        this.paymentPolicyEnum = paymentPolicyEnum;
    }

    @Override
    public void execute() {
        PremiumSnapshot premiumSnapshot = getPremiumSnapshot(registration);
        try {
            tryExecute(premiumSnapshot);
        } catch (PaymentException exception) {
            publishEvent(new AddPaymentFailedEvent(paymentId, date, amount, exception));
            throw new RollbackException(exception);
        }
    }

    @Override
    protected void reexecute(LocalDateTime registration) throws ReexecutionException {
        PremiumSnapshot premiumSnapshot = getPremiumSnapshot(registration);
        try {
            tryExecute(premiumSnapshot);
        } catch (PaymentException exception) {
            throw new ReexecutionException(exception, "Failed add payment {} during reexecution!", paymentId.getId());
        }
    }

    @Override
    public void publishFailedEvent(ReexecutionException exception) {
        publishEvent(new AddPaymentFailedEvent(paymentId, date, amount, exception));
    }

    private void tryExecute(PremiumSnapshot premiumSnapshot) throws PaymentException {
        Period period = getPeriod();
        PaymentPolicy paymentPolicy = PaymentPolicyFactory.create(paymentPolicyEnum, premiumSnapshot);
        paymentPolicy.pay(period, date, amount);
    }

}