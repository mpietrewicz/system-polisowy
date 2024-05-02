package pl.mpietrewicz.sp.modules.finance.domain.payment;

import pl.mpietrewicz.sp.ddd.annotations.domain.DomainFactory;
import pl.mpietrewicz.sp.ddd.canonicalmodel.publishedlanguage.AggregateId;
import pl.mpietrewicz.sp.ddd.sharedkernel.valueobject.PositiveAmount;

import java.time.LocalDate;

import static pl.mpietrewicz.sp.ddd.canonicalmodel.publishedlanguage.PaymentPolicyEnum.CONTINUATION;
import static pl.mpietrewicz.sp.ddd.canonicalmodel.publishedlanguage.PaymentPolicyEnum.RENEWAL_WITH_UNDERPAYMENT;

@DomainFactory
public class PaymentFactory {

    public Payment createPayment(AggregateId contractId, PositiveAmount payment, LocalDate date) {
        AggregateId aggregateId = AggregateId.generate();
        return new Payment(aggregateId, contractId, payment, date, RENEWAL_WITH_UNDERPAYMENT);
    }

    public Payment createSubsidy(AggregateId contractId, PositiveAmount subsidy, LocalDate date) {
        AggregateId aggregateId = AggregateId.generate();
        return new Payment(aggregateId, contractId, subsidy, date, CONTINUATION);
    }

}