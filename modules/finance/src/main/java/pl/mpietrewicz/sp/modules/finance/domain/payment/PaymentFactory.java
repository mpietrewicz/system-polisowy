package pl.mpietrewicz.sp.modules.finance.domain.payment;

import pl.mpietrewicz.sp.ddd.annotations.domain.DomainFactory;
import pl.mpietrewicz.sp.ddd.canonicalmodel.publishedlanguage.AggregateId;
import pl.mpietrewicz.sp.ddd.sharedkernel.valueobject.Amount;

import java.time.LocalDate;

import static pl.mpietrewicz.sp.ddd.canonicalmodel.publishedlanguage.PaymentPolicyEnum.CONTINUATION;
import static pl.mpietrewicz.sp.ddd.canonicalmodel.publishedlanguage.PaymentPolicyEnum.RENEWAL_WITH_UNDERPAYMENT;

@DomainFactory
public class PaymentFactory {

    public Payment createPayment(AggregateId contractId, Amount amount, LocalDate date) {
        AggregateId aggregateId = AggregateId.generate();
        return new Payment(aggregateId, contractId, amount, date, RENEWAL_WITH_UNDERPAYMENT);
    }

    public Payment createFunding(AggregateId contractId, Amount amount, LocalDate date) {
        AggregateId aggregateId = AggregateId.generate();
        return new Payment(aggregateId, contractId, amount, date, CONTINUATION);
    }

}