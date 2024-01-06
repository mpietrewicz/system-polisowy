
package pl.mpietrewicz.sp.ddd.canonicalmodel.publishedlanguage.snapshot;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import pl.mpietrewicz.sp.ddd.annotations.domain.ValueObject;
import pl.mpietrewicz.sp.ddd.canonicalmodel.publishedlanguage.AggregateId;
import pl.mpietrewicz.sp.ddd.canonicalmodel.publishedlanguage.ComponentStatus;

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
	private ContractData contractData;

	@Getter
	@Transient
	private LocalDate startDate;

	@Getter
	@Transient
	private ComponentStatus componentStatus;

	@SuppressWarnings("unused")
	private ComponentData() {}

	public ComponentData(AggregateId aggregateId) {
		this.aggregateId = aggregateId;
	}

	public ComponentData(AggregateId aggregateId, ContractData contractData, LocalDate startDate, ComponentStatus componentStatus) {
		this.aggregateId = aggregateId;
		this.contractData = contractData;
		this.startDate = startDate;
		this.componentStatus = componentStatus;
	}

	public AggregateId getContractId() {
		return contractData.getAggregateId();
	}

}