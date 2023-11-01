package pl.mpietrewicz.sp.modules.balance.domain.balance.operation;

import lombok.NoArgsConstructor;
import pl.mpietrewicz.sp.ddd.annotations.domain.ValueObject;
import pl.mpietrewicz.sp.modules.balance.domain.balance.Operation;
import pl.mpietrewicz.sp.ddd.canonicalmodel.publishedlanguage.PaymentPolicy;
import pl.mpietrewicz.sp.modules.balance.domain.balance.month.paymentpolicy.PaymentPolicyFactory;
import pl.mpietrewicz.sp.modules.balance.domain.balance.month.Month;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

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

    public AddPayment(LocalDateTime registration, LocalDate date, BigDecimal amount, PaymentPolicy paymentPolicy) {
        super(registration, date);
        this.amount = amount;
        this.paymentPolicy = paymentPolicy;
        this.type = ADD_PAYMENT;
    }

    @Override
    public void calculate() {
        PaymentPolicyFactory paymentPolicyFactory = new PaymentPolicyFactory();
        Month month = paymentPolicyFactory.create(paymentPolicy).getFirstMonthToPay(period, date);
        month.tryPay(amount);
    }

}