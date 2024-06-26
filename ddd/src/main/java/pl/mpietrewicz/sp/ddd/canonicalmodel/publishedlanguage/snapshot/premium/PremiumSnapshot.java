
package pl.mpietrewicz.sp.ddd.canonicalmodel.publishedlanguage.snapshot.premium;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import pl.mpietrewicz.sp.ddd.annotations.domain.ValueObject;
import pl.mpietrewicz.sp.ddd.canonicalmodel.publishedlanguage.AggregateId;
import pl.mpietrewicz.sp.ddd.sharedkernel.valueobject.PositiveAmount;

import javax.persistence.AttributeOverride;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.OneToMany;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.List;
import java.util.Optional;

@ValueObject
@Getter
@Builder
@EqualsAndHashCode(of = {"premiumId", "timestamp" })
public class PremiumSnapshot {

	@Embedded
	@AttributeOverride(name = "aggregateId", column = @Column(name = "premiumId", nullable = false))
	private AggregateId premiumId;

	private LocalDateTime timestamp;

	private AggregateId contractId;

	@OneToMany(cascade = CascadeType.ALL)
	private List<ComponentPremiumSnapshot> componentPremiumSnapshots;

	public PositiveAmount getAmountAt(LocalDate date) {
		return componentPremiumSnapshots.stream()
				.map(cps -> cps.getPremiumAt(date))
				.filter(Optional::isPresent)
				.map(Optional::get)
				.reduce(PositiveAmount::add)
				.orElseThrow(() -> new IllegalStateException("No any premium at date: " + date));
	}

	public PositiveAmount getPremiumAt(YearMonth month) {
		return getAmountAt(month.atDay(1));
	}

	public PositiveAmount getCurrentPremium() {
		return componentPremiumSnapshots.stream()
				.map(ComponentPremiumSnapshot::getCurrentPremium)
				.reduce(PositiveAmount::add)
				.orElseThrow();
	}

	public PositiveAmount getCurrentPremium(AggregateId componentId) {
		return componentPremiumSnapshots.stream()
				.filter(cps -> cps.getComponentId().equals(componentId))
				.map(ComponentPremiumSnapshot::getCurrentPremium)
				.findAny()
				.orElseThrow();
	}

	public LocalDate getValidFrom() {
		return componentPremiumSnapshots.stream()
				.map(ComponentPremiumSnapshot::getValidFrom)
				.max(LocalDate::compareTo)
				.orElseThrow();
	}

	public LocalDate getValidFrom(AggregateId componentId) {
		return componentPremiumSnapshots.stream()
				.filter(cps -> cps.getComponentId().equals(componentId))
				.findAny()
				.map(ComponentPremiumSnapshot::getValidFrom)
				.orElseThrow();
	}


}