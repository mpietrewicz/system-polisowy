package pl.mpietrewicz.sp.modules.balance.domain.balance.operation;

import lombok.NoArgsConstructor;
import pl.mpietrewicz.sp.ddd.annotations.domain.ValueObject;
import pl.mpietrewicz.sp.ddd.canonicalmodel.publishedlanguage.PaymentPolicy;
import pl.mpietrewicz.sp.modules.balance.domain.balance.month.Month;
import pl.mpietrewicz.sp.modules.balance.domain.balance.month.PaymentPolicyInterface;
import pl.mpietrewicz.sp.modules.balance.domain.balance.month.paymentpolicy.PaymentPolicyFactory;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import java.math.BigDecimal;
import java.time.LocalDate;

import static pl.mpietrewicz.sp.modules.balance.domain.balance.OperationType.ADD_PAYMENT;

@ValueObject
@Entity
@DiscriminatorValue("ADD_PAYMENT")
@NoArgsConstructor
public class AddPayment extends Operation {

    private BigDecimal amount;

    private PaymentPolicy paymentPolicy;

    public AddPayment(LocalDate date, BigDecimal amount, PaymentPolicy paymentPolicy) {
        super(date);
        this.amount = amount;
        this.paymentPolicy = paymentPolicy;
        this.type = ADD_PAYMENT;
    }

    @Override
    public void execute() {
        PaymentPolicyFactory paymentPolicyFactory = new PaymentPolicyFactory();
        PaymentPolicyInterface paymentPolicy = paymentPolicyFactory.create(this.paymentPolicy);
        Month month = paymentPolicy.getFirstMonthToPay(period, date); // todo: pozwlić na dodanie nowych okresów z przerwą (gdy wznowienie)
        month.tryPay(amount);
    }

}