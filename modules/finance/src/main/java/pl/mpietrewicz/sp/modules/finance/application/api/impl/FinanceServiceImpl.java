package pl.mpietrewicz.sp.modules.finance.application.api.impl;

import lombok.RequiredArgsConstructor;
import pl.mpietrewicz.sp.ddd.annotations.application.ApplicationService;
import pl.mpietrewicz.sp.ddd.canonicalmodel.events.PaymentAddedEvent;
import pl.mpietrewicz.sp.ddd.canonicalmodel.events.PaymentRefundedEvent;
import pl.mpietrewicz.sp.ddd.canonicalmodel.publishedlanguage.AggregateId;
import pl.mpietrewicz.sp.ddd.canonicalmodel.publishedlanguage.snapshot.RefundData;
import pl.mpietrewicz.sp.ddd.sharedkernel.valueobject.Amount;
import pl.mpietrewicz.sp.ddd.support.domain.DomainEventPublisher;
import pl.mpietrewicz.sp.modules.finance.application.api.FinanceService;
import pl.mpietrewicz.sp.modules.finance.domain.payment.Payment;
import pl.mpietrewicz.sp.modules.finance.domain.payment.PaymentFactory;
import pl.mpietrewicz.sp.modules.finance.domain.refund.Refund;
import pl.mpietrewicz.sp.modules.finance.domain.refund.RefundFactory;
import pl.mpietrewicz.sp.modules.finance.infrastructure.repo.PaymentRepository;
import pl.mpietrewicz.sp.modules.finance.infrastructure.repo.RefundRepository;

import java.time.LocalDate;

@ApplicationService(boundedContext = "finance", transactionManager = "financeTransactionManager")
@RequiredArgsConstructor
public class FinanceServiceImpl implements FinanceService {

    private final PaymentRepository paymentRepository;
    private final PaymentFactory paymentFactory;
    private final DomainEventPublisher domainEventPublisher;
    private final RefundFactory refundFactory;
    private final RefundRepository refundRepository;

    @Override
    public void addPayment(AggregateId contractId, Amount amount, LocalDate date) {
        Payment payment = paymentFactory.createPayment(contractId, amount, date);

        PaymentAddedEvent event = new PaymentAddedEvent(payment.generateSnapshot(), payment.getPaymentPolicy());
        domainEventPublisher.publish(event);

        paymentRepository.save(payment);
    }

    @Override
    public void refundPayment(AggregateId paymentId) {
        Payment payment = paymentRepository.load(paymentId);
        RefundData refundData = payment.refund();
        addRefund(refundData);

        PaymentRefundedEvent event = new PaymentRefundedEvent(refundData);
        domainEventPublisher.publish(event);
    }

    @Override
    public void addFunding(AggregateId contractId, Amount amount, LocalDate date) {
        Payment payment = paymentFactory.createFunding(contractId, amount, date);

        PaymentAddedEvent event = new PaymentAddedEvent(payment.generateSnapshot(), payment.getPaymentPolicy());
        domainEventPublisher.publish(event);

        paymentRepository.save(payment);
    }

    @Override
    public void addRefund(RefundData refundData) {
        Refund refund = refundFactory.create(refundData);

        refundRepository.save(refund);
    }

}