package pl.mpietrewicz.sp.modules.oldbalance.domain.balance;

import pl.mpietrewicz.sp.modules.oldbalance.domain.balance.operation.Operation;
import pl.mpietrewicz.sp.modules.oldbalance.domain.balance.operation.Payment;
import pl.mpietrewicz.sp.modules.oldbalance.domain.balance.operation.Refund;
import pl.mpietrewicz.sp.modules.oldbalance.domain.balance.policy.operationcalculation.PaymentCalculationPolicyEnum;
import pl.mpietrewicz.sp.ddd.annotations.domain.ValueObject;
import pl.mpietrewicz.sp.ddd.canonicalmodel.publishedlanguage.snapshot.PaymentData;
import pl.mpietrewicz.sp.ddd.canonicalmodel.publishedlanguage.snapshot.RefundData;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static pl.mpietrewicz.sp.modules.oldbalance.domain.balance.policy.operationcalculation.PaymentCalculationPolicyEnum.ERASE;

@ValueObject
@Embeddable
public class Operations { // todo: może to jest kandydat na oddzialny agregat?

    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "balance_id")
    private List<Operation> operations = new ArrayList<>();

    public Operations() {
    }

    public Operations(List<Operation> operations) {
        this.operations = operations;
    }

    public void addPayment(PaymentData paymentData, Periods periods, PaymentCalculationPolicyEnum paymentCalculationPolicy) {
        Payment payment = createPayment(paymentData, paymentCalculationPolicy);
        operations.add(payment);
        payment.allocate(this, periods);
    }

    public void addRefund(RefundData refundData, Periods periods, PaymentCalculationPolicyEnum paymentCalculationPolicy) {
        Refund refund = createRefund(refundData, paymentCalculationPolicy);
        operations.add(refund);
        refund.allocate(this, periods);
    }

    public void recalculate(YearMonth startMonth, Periods periods) {
        Optional<Payment> operation = getFirstPaymentCoveredBy(startMonth);
        operation.ifPresent(o -> {
            o.allocate(this, periods, ERASE);
        });
    }

    public List<Operation> getAll() {
        return operations;
    }

    private Payment createPayment(PaymentData paymentData, PaymentCalculationPolicyEnum paymentCalculationPolicy) {
        LocalDate date = paymentData.getDate();
        BigDecimal amount = paymentData.getAmount();
        return new Payment(date, amount, paymentCalculationPolicy);
    }

    private Refund createRefund(RefundData refundData, PaymentCalculationPolicyEnum paymentCalculationPolicy) {
        LocalDate date = refundData.getDate();
        BigDecimal amount = refundData.getAmount();
        return new Refund(date, amount, paymentCalculationPolicy);
    }

    private Optional<Payment> getFirstPaymentCoveredBy(YearMonth month) {
        return getPayments().stream()
                .sorted(Payment::compareDate)
                .filter(payment -> payment.isCovering(month))
                .findFirst();
    }

    public List<Operation> getSortedOperationsAfter(Operation operation) {
        return operations.stream()
                .filter(o -> o.isAfter(operation))
                .sorted(Operation::compareDate)
                .collect(Collectors.toList());
    }

    public Optional<Payment> getPaymentBefore(Operation operation) {
        return getPayments().stream()
                .filter(o -> o.isBefore(operation))
                .max(Operation::compareDate);
    }

    private List<Payment> getPayments() {
        return operations.stream()
                .filter(Operation::isPayment)
                .map(Payment.class::cast)
                .collect(Collectors.toList());
    }

    private List<Refund> getRefunds() {
        return operations.stream()
                .filter(Operation::isRefund)
                .map(Refund.class::cast)
                .collect(Collectors.toList());
    }

    public void addAll(List<Payment> payments) { // todo: do zmiany - tak chyba nie może być
        operations.addAll(payments);
    }

}