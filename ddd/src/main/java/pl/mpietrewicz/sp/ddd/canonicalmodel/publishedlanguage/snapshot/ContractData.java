
package pl.mpietrewicz.sp.ddd.canonicalmodel.publishedlanguage.snapshot;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import pl.mpietrewicz.sp.ddd.annotations.domain.ValueObject;
import pl.mpietrewicz.sp.ddd.canonicalmodel.publishedlanguage.AggregateId;
import pl.mpietrewicz.sp.ddd.canonicalmodel.publishedlanguage.Frequency;
import pl.mpietrewicz.sp.ddd.canonicalmodel.publishedlanguage.PaymentPolicyEnum;

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
	private LocalDate contractStartDate;

	@Transient
	@Enumerated(EnumType.STRING)
	private Frequency frequency;

	@Transient
	@Enumerated(EnumType.STRING)
	private PaymentPolicyEnum paymentPolicyEnum;

	@Transient
	private YearMonth accountingMonth;

	@SuppressWarnings("unused")
	private ContractData(){}

	public ContractData(AggregateId aggregateId, LocalDate contractStartDate, Frequency frequency,
                        PaymentPolicyEnum paymentPolicyEnum, YearMonth accountingMonth) {
		this.aggregateId = aggregateId;
		this.contractStartDate = contractStartDate;
		this.frequency = frequency;
		this.paymentPolicyEnum = paymentPolicyEnum;
		this.accountingMonth = accountingMonth;
	}

}