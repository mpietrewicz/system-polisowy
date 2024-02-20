package pl.mpietrewicz.sp.ddd.support.infrastructure.repo;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import pl.mpietrewicz.sp.ddd.canonicalmodel.publishedlanguage.AggregateId;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.MappedSuperclass;
import javax.persistence.Version;

/**
 * @author Slawek
 * 
 */
@Component
@Scope("prototype")//created in domain factories, not in spring container, therefore we don't want eager creation
@MappedSuperclass
public abstract class BaseAggregateRoot {
	public static enum AggregateStatus {
		ACTIVE, ARCHIVE
	}

	@EmbeddedId
	@AttributeOverrides({
		  @AttributeOverride(name = "idValue", column = @Column(name = "aggregateId", nullable = false))})
	public AggregateId aggregateId;

	@Version
	public Long version;

	@Enumerated(EnumType.ORDINAL)
	private AggregateStatus aggregateStatus = AggregateStatus.ACTIVE;
	
	public void markAsRemoved() {
		aggregateStatus = AggregateStatus.ARCHIVE;
	}

	public AggregateId getAggregateId() {
		return aggregateId;
	}

	public Long getVersion() {
		return version;
	}

	public boolean isRemoved() {
		return aggregateStatus == AggregateStatus.ARCHIVE;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (obj instanceof BaseAggregateRoot) {
			BaseAggregateRoot other = (BaseAggregateRoot) obj;
			if (other.aggregateId == null)
				return false;
			return other.aggregateId.equals(aggregateId);
		}
		
		return false;
	}
	
	@Override
	public int hashCode() {	
		return aggregateId.hashCode();
	}
}