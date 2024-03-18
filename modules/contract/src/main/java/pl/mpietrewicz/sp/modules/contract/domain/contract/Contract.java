
package pl.mpietrewicz.sp.modules.contract.domain.contract;

import lombok.Getter;
import pl.mpietrewicz.sp.ddd.annotations.domain.AggregateRoot;
import pl.mpietrewicz.sp.ddd.canonicalmodel.publishedlanguage.AggregateId;
import pl.mpietrewicz.sp.ddd.canonicalmodel.publishedlanguage.Frequency;
import pl.mpietrewicz.sp.ddd.canonicalmodel.publishedlanguage.snapshot.ContractData;
import pl.mpietrewicz.sp.ddd.support.infrastructure.repo.BaseAggregateRoot;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import java.time.LocalDate;

@Entity
@AggregateRoot
@Getter
public class Contract extends BaseAggregateRoot {

	private LocalDate start;

	@Enumerated(EnumType.STRING)
	private Frequency frequency;

	private LocalDate end;

	public Contract() {
	}

	public Contract(AggregateId aggregateId, LocalDate start, Frequency frequency) {
		this.aggregateId = aggregateId;
		this.start = start;
		this.frequency = frequency;
	}

	public void end(LocalDate date) {
		end = date;
	}

	public void cancelEnd() {
		end = null;
	}

	public ContractData generateSnapshot() {
		return new ContractData(aggregateId, start, frequency);
	}

}