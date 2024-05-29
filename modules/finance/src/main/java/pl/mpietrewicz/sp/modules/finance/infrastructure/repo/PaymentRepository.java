
package pl.mpietrewicz.sp.modules.finance.infrastructure.repo;

import pl.mpietrewicz.sp.ddd.annotations.domain.DomainRepository;
import pl.mpietrewicz.sp.ddd.canonicalmodel.publishedlanguage.AggregateId;
import pl.mpietrewicz.sp.modules.finance.domain.payment.Payment;

import java.util.List;

@DomainRepository
public interface PaymentRepository {

	Payment load(AggregateId paymentId);
	
	void save(Payment payment);

	List<Payment> findBy(AggregateId contractId);

	List<Payment> findBy(AggregateId contractId, AggregateId paymentId);

}