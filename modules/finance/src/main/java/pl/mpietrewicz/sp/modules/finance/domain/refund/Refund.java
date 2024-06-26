
package pl.mpietrewicz.sp.modules.finance.domain.refund;

import lombok.Getter;
import pl.mpietrewicz.sp.ddd.annotations.domain.AggregateRoot;
import pl.mpietrewicz.sp.ddd.canonicalmodel.publishedlanguage.AggregateId;
import pl.mpietrewicz.sp.ddd.sharedkernel.valueobject.PositiveAmount;
import pl.mpietrewicz.sp.ddd.support.infrastructure.repo.BaseAggregateRoot;

import javax.persistence.AttributeOverride;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import java.time.LocalDate;
import java.time.LocalDateTime;

@AggregateRoot
@Getter
@Entity
public class Refund extends BaseAggregateRoot {

	@Embedded
	@AttributeOverride(name = "value", column = @Column(name = "refund"))
	private PositiveAmount refund;

	private LocalDate date;

	private final LocalDateTime registration = LocalDateTime.now();

	@Embedded
	@AttributeOverride(name = "aggregateId", column = @Column(name = "contractId", nullable = false))
	private AggregateId contractId;

	private Refund(){}
	
	public Refund(AggregateId aggregateId, AggregateId contractId, PositiveAmount refund, LocalDate date) {
		this.aggregateId = aggregateId;
		this.contractId = contractId;
		this.refund = refund;
		this.date = date;
	}

}