package pl.mpietrewicz.sp.modules.balance.domain.balance.operation.type;

import lombok.NoArgsConstructor;
import pl.mpietrewicz.sp.ddd.annotations.domain.ValueObject;
import pl.mpietrewicz.sp.ddd.canonicalmodel.publishedlanguage.PaymentPolicyEnum;
import pl.mpietrewicz.sp.ddd.canonicalmodel.publishedlanguage.snapshot.premium.PremiumSnapshot;
import pl.mpietrewicz.sp.ddd.sharedkernel.Amount;
import pl.mpietrewicz.sp.modules.balance.domain.balance.operation.Operation;
import pl.mpietrewicz.sp.modules.balance.domain.balance.operation.PaymentData;
import pl.mpietrewicz.sp.modules.balance.domain.balance.paymentpolicy.PaymentPolicy;
import pl.mpietrewicz.sp.modules.balance.domain.balance.paymentpolicy.PaymentPolicyFactory;

import javax.persistence.AttributeOverride;
import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import java.time.LocalDate;

import static pl.mpietrewicz.sp.modules.balance.domain.balance.operation.OperationType.ADD_PAYMENT;

@ValueObject
@Entity
@DiscriminatorValue("ADD_PAYMENT")
@NoArgsConstructor
public class AddPayment extends Operation {

    @Embedded
    @AttributeOverride(name = "value", column = @Column(name = "amount"))
    private Amount amount;

    private PaymentPolicyEnum paymentPolicyEnum;

    public AddPayment(LocalDate date, Amount amount, PaymentPolicyEnum paymentPolicyEnum) {
        super(date);
        this.amount = amount;
        this.paymentPolicyEnum = paymentPolicyEnum;
        this.type = ADD_PAYMENT;
    }

    @Override
    public void execute(PremiumSnapshot premiumSnapshot) {
        PaymentData paymentData = new PaymentData(date, amount);
        PaymentPolicy paymentPolicy = PaymentPolicyFactory.create(paymentPolicyEnum, premiumSnapshot);
        period.tryPay(paymentPolicy, paymentData, premiumSnapshot);
    }

}