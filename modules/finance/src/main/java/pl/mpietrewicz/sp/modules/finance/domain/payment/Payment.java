
package pl.mpietrewicz.sp.modules.finance.domain.payment;

import lombok.Getter;
import pl.mpietrewicz.sp.ddd.annotations.domain.AggregateRoot;
import pl.mpietrewicz.sp.ddd.canonicalmodel.publishedlanguage.AggregateId;
import pl.mpietrewicz.sp.ddd.canonicalmodel.publishedlanguage.PaymentPolicyEnum;
import pl.mpietrewicz.sp.ddd.canonicalmodel.publishedlanguage.snapshot.PaymentData;
import pl.mpietrewicz.sp.ddd.canonicalmodel.publishedlanguage.snapshot.RefundData;
import pl.mpietrewicz.sp.ddd.sharedkernel.valueobject.PositiveAmount;
import pl.mpietrewicz.sp.ddd.support.infrastructure.repo.BaseAggregateRoot;

import javax.persistence.AttributeOverride;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import java.time.LocalDate;
import java.time.LocalDateTime;

@AggregateRoot
@Getter
@Entity
public class Payment extends BaseAggregateRoot {

	@Embedded
	@AttributeOverride(name = "value", column = @Column(name = "payment"))
	private PositiveAmount payment;

	private LocalDate date;

	private final LocalDateTime registration = LocalDateTime.now();

	@Embedded
	@AttributeOverride(name = "aggregateId", column = @Column(name = "contractId", nullable = false))
	private AggregateId contractId;

	@Enumerated(EnumType.STRING)
	private PaymentPolicyEnum paymentPolicyEnum;

	private boolean refunded;

	@Embedded
	@AttributeOverride(name = "aggregateId", column = @Column(name = "refundId"))
	private AggregateId refundId = new AggregateId("");

	@SuppressWarnings("unused")
	private Payment(){}
	
	public Payment(AggregateId aggregateId, AggregateId contractId, PositiveAmount payment, LocalDate date,
				   PaymentPolicyEnum paymentPolicyEnum) {
		this.aggregateId = aggregateId;
		this.contractId = contractId;
		this.payment = payment;
		this.date = date;
		this.paymentPolicyEnum = paymentPolicyEnum;
	}

	public PaymentData generateSnapshot() {
		return new PaymentData(aggregateId, contractId, date, payment);
	}

	public LocalDate getDate() {
		return date;
	}

	public PaymentPolicyEnum getPaymentPolicy() {
		return paymentPolicyEnum;
	}

	public RefundData refund() {
		refunded = true;
		refundId = AggregateId.generate();
		return new RefundData(refundId, contractId, LocalDate.now(), payment);
	}

}