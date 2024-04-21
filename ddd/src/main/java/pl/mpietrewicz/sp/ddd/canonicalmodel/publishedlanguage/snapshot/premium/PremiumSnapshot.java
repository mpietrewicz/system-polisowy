
package pl.mpietrewicz.sp.ddd.canonicalmodel.publishedlanguage.snapshot.premium;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import pl.mpietrewicz.sp.ddd.annotations.domain.ValueObject;
import pl.mpietrewicz.sp.ddd.canonicalmodel.publishedlanguage.AggregateId;
import pl.mpietrewicz.sp.ddd.canonicalmodel.publishedlanguage.snapshot.ContractData;
import pl.mpietrewicz.sp.ddd.sharedkernel.Amount;
import pl.mpietrewicz.sp.ddd.sharedkernel.PositiveAmount;

import javax.persistence.AttributeOverride;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.OneToMany;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.List;

import static pl.mpietrewicz.sp.ddd.sharedkernel.PositiveAmount.ZERO;

@ValueObject
@Getter
@Builder
@EqualsAndHashCode(of = {"premiumId", "timestamp" })
public class PremiumSnapshot {

	@Embedded
	@AttributeOverride(name = "aggregateId", column = @Column(name = "premiumId", nullable = false))
	private AggregateId premiumId;

	private LocalDateTime timestamp;

	private ContractData contractData;

	@OneToMany(cascade = CascadeType.ALL)
	private List<ComponentPremiumSnapshot> componentPremiumSnapshots;

	public PositiveAmount getAmountAt(LocalDate date) {
		Amount amount = componentPremiumSnapshots.stream()
				.map(cps -> cps.getPremiumAt(date))
				.reduce(ZERO, Amount::add);

		if (amount.isPositive()) {
			return amount.castToPositive();
		} else {
			throw new IllegalStateException("No any premium at date: " + date);
		}
	}

	public Amount getPremiumAt(YearMonth month) {
		return componentPremiumSnapshots.stream()
				.map(cps -> cps.getPremiumAt(month))
				.reduce(ZERO, Amount::add);
	}

}