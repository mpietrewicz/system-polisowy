package pl.mpietrewicz.sp.modules.contract.domain.premium;

import pl.mpietrewicz.sp.ddd.annotations.domain.AggregateRoot;
import pl.mpietrewicz.sp.ddd.canonicalmodel.events.PremiumChangedEvent;
import pl.mpietrewicz.sp.ddd.canonicalmodel.publishedlanguage.AggregateId;
import pl.mpietrewicz.sp.ddd.canonicalmodel.publishedlanguage.snapshot.premium.ComponentPremiumSnapshot;
import pl.mpietrewicz.sp.ddd.canonicalmodel.publishedlanguage.snapshot.premium.PremiumSnapshot;
import pl.mpietrewicz.sp.ddd.sharedkernel.valueobject.PositiveAmount;
import pl.mpietrewicz.sp.ddd.support.domain.DomainEventPublisher;
import pl.mpietrewicz.sp.ddd.support.infrastructure.repo.BaseAggregateRoot;
import pl.mpietrewicz.sp.modules.contract.domain.premium.component.AdditionalComponentPremium;
import pl.mpietrewicz.sp.modules.contract.domain.premium.component.BasicComponentPremium;
import pl.mpietrewicz.sp.modules.contract.domain.premium.component.ComponentPremium;
import pl.mpietrewicz.sp.modules.contract.domain.premium.operation.AddPremium;

import javax.inject.Inject;
import javax.persistence.AttributeOverride;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.Transient;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static java.util.stream.Collectors.toList;

@Entity
@AggregateRoot
public class Premium extends BaseAggregateRoot {

    @Embedded
    @AttributeOverride(name = "aggregateId", column = @Column(name = "contractId", nullable = false))
    private AggregateId contractId;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER, orphanRemoval = true)
    @JoinColumn(name = "premium_id")
    private final List<ComponentPremium> componentPremiums = new ArrayList<>();

    @Transient
    @Inject
    protected DomainEventPublisher eventPublisher;

    public Premium() {
    }

    public Premium(AggregateId aggregateId, AggregateId contractId, BasicComponentPremium basicComponentPremium) {
        this.aggregateId = aggregateId;
        this.contractId = contractId;
        this.componentPremiums.add(basicComponentPremium);
    }

    public void add(AggregateId componentId, LocalDate date, PositiveAmount premium, ChangePremiumPolicyEnum changePremiumPolicyEnum) {
        LocalDateTime now = LocalDateTime.now();
        AddPremium addPremium = new AddPremium(date, premium, now);
        ComponentPremium additionalComponentPremium = new AdditionalComponentPremium(componentId, addPremium,
                changePremiumPolicyEnum);
        componentPremiums.add(additionalComponentPremium);

        sentEvent(date, now);
    }

    public void cancel(AggregateId componentId) {
        LocalDateTime now = LocalDateTime.now();
        ComponentPremium componentPremium = getComponentPremium(componentId)
                .orElseThrow(() -> new IllegalStateException("No component found for premium change"));

        LocalDate canceledAddDate = componentPremium.cancel(now);

        sentEvent(canceledAddDate, now);
    }

    public void change(AggregateId componentId, LocalDate date, PositiveAmount premium) {
        LocalDateTime now = LocalDateTime.now();
        ComponentPremium componentPremium = getComponentPremium(componentId)
                .orElseThrow(() -> new IllegalStateException("No component found for premium change"));

        date = YearMonth.from(date).atDay(1);
        componentPremium.changePremium(date, premium, now);

        sentEvent(date, now);
    }

    public void delete(AggregateId componentId, LocalDate date) {
        LocalDateTime now = LocalDateTime.now();
        ComponentPremium componentPremium = getComponentPremium(componentId)
                .orElseThrow(() -> new IllegalStateException("No component found for premium delete"));

        componentPremium.deletePremium(date, now);

        sentEvent(date.plusDays(1), now);
    }

    private Optional<ComponentPremium> getComponentPremium(AggregateId componentId) {
        return componentPremiums.stream()
                .filter(cp -> cp.applay(componentId))
                .findAny();
    }

    public PremiumSnapshot generateSnapshot(LocalDateTime timestamp) {
        List<ComponentPremiumSnapshot> componentPremiumSnapshots = componentPremiums.stream()
                .map(cp -> cp.generateSnapshot(timestamp))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(toList());

        return PremiumSnapshot.builder()
                .premiumId(aggregateId)
                .timestamp(timestamp)
                .contractId(contractId)
                .componentPremiumSnapshots(componentPremiumSnapshots)
                .build();
    }

    private void sentEvent(LocalDate date, LocalDateTime timestamp) {
        PremiumChangedEvent event = new PremiumChangedEvent(contractId, date, timestamp);
        eventPublisher.publish(event);
    }

}