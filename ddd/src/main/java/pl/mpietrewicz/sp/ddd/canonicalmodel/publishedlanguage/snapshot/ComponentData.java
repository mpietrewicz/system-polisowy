
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
import java.time.LocalDate;

@ValueObject
@Embeddable
@EqualsAndHashCode(of = "aggregateId")
public class ComponentData {

	@Getter
	@Embedded
	@AttributeOverrides({
			  @AttributeOverride(name = "aggregateId", column = @Column(name = "componentId", nullable = false))})
	private AggregateId aggregateId;

	@Transient
	private AggregateId contractId;

	@Getter
	@Transient
	private LocalDate startDate;

	@SuppressWarnings("unused")
	private ComponentData() {}

	public ComponentData(AggregateId aggregateId) {
		this.aggregateId = aggregateId;
	}

	public ComponentData(AggregateId aggregateId, AggregateId contractId, LocalDate startDate) {
		this.aggregateId = aggregateId;
		this.contractId = contractId;
		this.startDate = startDate;
	}

	public AggregateId getContractId() {
		return contractId;
	}

}