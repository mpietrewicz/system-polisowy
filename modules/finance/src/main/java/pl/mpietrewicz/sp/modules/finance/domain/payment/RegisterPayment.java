
package pl.mpietrewicz.sp.modules.finance.domain.payment;

import pl.mpietrewicz.sp.ddd.annotations.domain.AggregateRoot;
import pl.mpietrewicz.sp.ddd.canonicalmodel.publishedlanguage.AggregateId;
import pl.mpietrewicz.sp.ddd.canonicalmodel.publishedlanguage.snapshot.ContractData;
import pl.mpietrewicz.sp.ddd.canonicalmodel.publishedlanguage.snapshot.PaymentData;
import pl.mpietrewicz.sp.ddd.sharedkernel.Amount;
import pl.mpietrewicz.sp.ddd.support.infrastructure.repo.BaseAggregateRoot;

import javax.persistence.AttributeOverride;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import java.time.LocalDate;
import java.time.LocalDateTime;

@AggregateRoot
@Entity
public class RegisterPayment extends BaseAggregateRoot {

	@Embedded
	@AttributeOverride(name = "value", column = @Column(name = "amount"))
	private Amount amount;

	private LocalDate date;

	private final LocalDateTime register = LocalDateTime.now();

	@Embedded
	private ContractData contractData;

	@SuppressWarnings("unused")
	private RegisterPayment(){}
	
	public RegisterPayment(AggregateId aggregateId, ContractData contractData, Amount amount, LocalDate date) {
		this.aggregateId = aggregateId;
		this.contractData = contractData;
		this.amount = amount;
		this.date = date;
	}

	public PaymentData generateSnapshot() {
		return new PaymentData(aggregateId, contractData.getAggregateId(), date, amount);
	}

	public LocalDate getDate() {
		return date;
	}

	public ContractData getContractData() {
		return contractData;
	}

}