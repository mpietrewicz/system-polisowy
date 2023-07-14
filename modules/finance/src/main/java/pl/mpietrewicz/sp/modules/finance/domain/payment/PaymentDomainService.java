package pl.mpietrewicz.sp.modules.finance.domain.payment;

import lombok.RequiredArgsConstructor;
import pl.mpietrewicz.sp.ddd.annotations.domain.DomainService;
import pl.mpietrewicz.sp.ddd.canonicalmodel.events.PaymentAddedEvent;
import pl.mpietrewicz.sp.ddd.canonicalmodel.publishedlanguage.snapshot.ContractData;
import pl.mpietrewicz.sp.ddd.sharedkernel.PaymentPolicyEnum;
import pl.mpietrewicz.sp.ddd.support.domain.DomainEventPublisher;

import java.math.BigDecimal;
import java.time.LocalDate;

@DomainService
@RequiredArgsConstructor
public class PaymentDomainService {

    private final PaymentFactory paymentFactory;
    private final DomainEventPublisher domainEventPublisher;

    public RegisterPayment createPayment(ContractData contractData, BigDecimal amount, LocalDate paymentDate) {
        RegisterPayment registerPayment = paymentFactory.createPayment(contractData, amount, paymentDate);
        PaymentPolicyEnum paymentPolicyEnum = PaymentPolicyEnum.WITH_RENEWAL; // todo: w przyszłosci wybierać z agregatu Payment lub z UI

        domainEventPublisher.publish(new PaymentAddedEvent(registerPayment.generateSnapshot(), paymentPolicyEnum));
        return registerPayment;
    }

}