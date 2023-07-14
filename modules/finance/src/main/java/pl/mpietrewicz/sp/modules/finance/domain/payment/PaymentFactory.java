package pl.mpietrewicz.sp.modules.finance.domain.payment;

import pl.mpietrewicz.sp.ddd.annotations.domain.DomainFactory;
import pl.mpietrewicz.sp.ddd.canonicalmodel.publishedlanguage.AggregateId;
import pl.mpietrewicz.sp.ddd.canonicalmodel.publishedlanguage.snapshot.ContractData;

import java.math.BigDecimal;
import java.time.LocalDate;

@DomainFactory
public class PaymentFactory {

    public RegisterPayment createPayment(ContractData contractData, BigDecimal amount, LocalDate date) {
        AggregateId aggregateId = AggregateId.generate();
        return new RegisterPayment(aggregateId, contractData, amount, date);
    }

}