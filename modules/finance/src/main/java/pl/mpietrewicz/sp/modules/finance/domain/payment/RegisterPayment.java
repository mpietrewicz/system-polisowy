
package pl.mpietrewicz.sp.modules.finance.domain.payment;

import pl.mpietrewicz.sp.ddd.annotations.domain.AggregateRoot;
import pl.mpietrewicz.sp.ddd.support.domain.BaseAggregateRoot;
import pl.mpietrewicz.sp.ddd.canonicalmodel.publishedlanguage.AggregateId;
import pl.mpietrewicz.sp.ddd.canonicalmodel.publishedlanguage.snapshot.ContractData;
import pl.mpietrewicz.sp.ddd.canonicalmodel.publishedlanguage.snapshot.PaymentData;

import javax.persistence.Embedded;
import javax.persistence.Entity;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@AggregateRoot
@Entity
public class RegisterPayment extends BaseAggregateRoot {

	private BigDecimal amount;
	private LocalDate date;
	private final LocalDateTime register = LocalDateTime.now();

	@Embedded
	private ContractData contractData;

	@SuppressWarnings("unused")
	private RegisterPayment(){}
	
	public RegisterPayment(AggregateId aggregateId, ContractData contractData, BigDecimal amount, LocalDate date) {
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

	public BigDecimal getAmount() {
		return amount;
	}

	public ContractData getContractData() {
		return contractData;
	}

}