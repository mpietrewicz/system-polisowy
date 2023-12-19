
package pl.mpietrewicz.sp.modules.finance.infrastructure.repo;

import pl.mpietrewicz.sp.ddd.annotations.domain.DomainRepository;
import pl.mpietrewicz.sp.ddd.canonicalmodel.publishedlanguage.AggregateId;
import pl.mpietrewicz.sp.modules.finance.domain.payment.RegisterPayment;

import java.util.List;

@DomainRepository
public interface PaymentRepository {

	RegisterPayment load(AggregateId paymentId);
	
	void save(RegisterPayment registerPayment);

	List<RegisterPayment> findAll(AggregateId contractId);
}