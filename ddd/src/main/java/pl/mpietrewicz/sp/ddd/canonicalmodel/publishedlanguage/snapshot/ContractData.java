
package pl.mpietrewicz.sp.ddd.canonicalmodel.publishedlanguage.snapshot;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import pl.mpietrewicz.sp.ddd.annotations.domain.ValueObject;
import pl.mpietrewicz.sp.ddd.canonicalmodel.publishedlanguage.AggregateId;
import pl.mpietrewicz.sp.ddd.canonicalmodel.publishedlanguage.Frequency;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.Embedded;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Transient;
import java.time.LocalDate;
import java.time.YearMonth;

/**
 * Contract's snapshot
 */
@ValueObject
@Embeddable
@Getter
@EqualsAndHashCode(of = "aggregateId")
public class ContractData {

	@Embedded
	@AttributeOverrides({
			  @AttributeOverride(name = "aggregateId", column = @Column(name = "contractId", nullable = false))})
	private AggregateId aggregateId;

	@Transient
	private LocalDate start;

	@Transient
	@Enumerated(EnumType.STRING)
	private Frequency frequency;

	@SuppressWarnings("unused")
	private ContractData() {}

	public ContractData(AggregateId aggregateId) {
		this.aggregateId = aggregateId;
	}

	public ContractData(AggregateId aggregateId, LocalDate start, Frequency frequency) {
		this.aggregateId = aggregateId;
		this.start = start;
		this.frequency = frequency;
	}

}