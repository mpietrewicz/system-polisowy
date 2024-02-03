package pl.mpietrewicz.sp.modules.finance.domain.payment;

import lombok.RequiredArgsConstructor;
import pl.mpietrewicz.sp.ddd.annotations.domain.DomainService;
import pl.mpietrewicz.sp.ddd.canonicalmodel.events.PaymentAddedEvent;
import pl.mpietrewicz.sp.ddd.canonicalmodel.publishedlanguage.snapshot.ContractData;
import pl.mpietrewicz.sp.ddd.canonicalmodel.publishedlanguage.PaymentPolicyEnum;
import pl.mpietrewicz.sp.ddd.sharedkernel.Amount;
import pl.mpietrewicz.sp.ddd.support.domain.DomainEventPublisher;

import java.time.LocalDate;

@DomainService
@RequiredArgsConstructor
public class PaymentDomainService {

    private final PaymentFactory paymentFactory;
    private final DomainEventPublisher domainEventPublisher;

    public RegisterPayment createPayment(ContractData contractData, Amount amount, LocalDate paymentDate) {
        RegisterPayment registerPayment = paymentFactory.createPayment(contractData, amount, paymentDate);
        PaymentPolicyEnum paymentPolicyEnum = contractData.getPaymentPolicyEnum();

        PaymentAddedEvent event = new PaymentAddedEvent(registerPayment.generateSnapshot(), paymentPolicyEnum);
        domainEventPublisher.publish(event, "PaymentServiceImpl");
        return registerPayment;
    }

}