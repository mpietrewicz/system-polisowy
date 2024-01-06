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
import java.time.YearMonth;

import static pl.mpietrewicz.sp.modules.balance.domain.balance.operation.OperationType.CHANGE_PREMIUM;

@ValueObject
@Entity
@DiscriminatorValue("CHANGE_PREMIUM")
@NoArgsConstructor
public class ChangePremium extends Operation {

    @Embedded
    @AttributeOverride(name = "value", column = @Column(name = "amount"))
    private Amount premium;

    public ChangePremium(LocalDate date, Amount premium) {
        super(date);
        this.premium = premium;
        this.type = CHANGE_PREMIUM;
    }

    @Override
    public void execute(PremiumSnapshot premiumSnapshot) {
        YearMonth monthOfChange = YearMonth.from(date);
        Amount refunded = period.tryRefundUpTo(monthOfChange);

        if (refunded.isPositive()) {
            PaymentPolicy paymentPolicy = PaymentPolicyFactory.create(PaymentPolicyEnum.CONTINUATION, premiumSnapshot);
            PaymentData paymentData = new PaymentData(date, refunded);
            period.tryPay(paymentPolicy, paymentData, premiumSnapshot);
        }
    }

    protected void execute() {
        throw new UnsupportedOperationException("Metoda nie obsługiwana w StartCalculating Operation");
    }

}