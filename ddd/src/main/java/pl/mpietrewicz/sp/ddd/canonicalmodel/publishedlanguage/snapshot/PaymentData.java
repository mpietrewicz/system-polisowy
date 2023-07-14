
package pl.mpietrewicz.sp.ddd.canonicalmodel.publishedlanguage.snapshot;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import pl.mpietrewicz.sp.ddd.annotations.domain.ValueObject;
import pl.mpietrewicz.sp.ddd.canonicalmodel.publishedlanguage.AggregateId;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.Embedded;
import javax.persistence.Transient;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Objects;

@ValueObject
@Embeddable
@Getter
@EqualsAndHashCode(of = "aggregateId")
public class PaymentData {

	@Embedded
	@AttributeOverrides({
			  @AttributeOverride(name = "aggregateId", column = @Column(name = "paymentId", nullable = false))})
	private AggregateId aggregateId;

	@Transient
	private AggregateId contractId;

	@Transient
	private LocalDate date;

	@Transient
	private BigDecimal amount;

	@SuppressWarnings("unused")
	private PaymentData(){}

	public PaymentData(AggregateId aggregateId, AggregateId contractId, LocalDate date, BigDecimal amount) {
		this.aggregateId = aggregateId;
		this.contractId = contractId;
		this.amount = amount;
		this.date = date;
	}

}