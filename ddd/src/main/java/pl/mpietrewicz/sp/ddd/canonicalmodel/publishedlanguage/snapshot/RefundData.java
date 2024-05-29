
package pl.mpietrewicz.sp.ddd.canonicalmodel.publishedlanguage.snapshot;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import pl.mpietrewicz.sp.ddd.annotations.domain.ValueObject;
import pl.mpietrewicz.sp.ddd.canonicalmodel.publishedlanguage.AggregateId;
import pl.mpietrewicz.sp.ddd.sharedkernel.valueobject.PositiveAmount;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.Embedded;
import javax.persistence.Transient;
import java.time.LocalDate;

@ValueObject
@Embeddable
@Getter
@EqualsAndHashCode(of = "aggregateId")
public class RefundData {

	@Embedded
	@AttributeOverrides({
			  @AttributeOverride(name = "aggregateId", column = @Column(name = "paymentId", nullable = false))})
	private AggregateId aggregateId;

	@Transient
	private AggregateId contractId;

	@Transient
	private LocalDate date;

	@Transient
	private PositiveAmount refund;

	@SuppressWarnings("unused")
	private RefundData(){}

	public RefundData(AggregateId aggregateId, AggregateId contractId, LocalDate date, PositiveAmount refund) {
		this.aggregateId = aggregateId;
		this.contractId = contractId;
		this.refund = refund;
		this.date = date;
	}

}