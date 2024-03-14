package pl.mpietrewicz.sp.modules.contract.domain.premium;

import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import pl.mpietrewicz.sp.ddd.annotations.domain.AggregateRoot;
import pl.mpietrewicz.sp.ddd.canonicalmodel.events.PremiumChangedEvent;
import pl.mpietrewicz.sp.ddd.canonicalmodel.publishedlanguage.AggregateId;
import pl.mpietrewicz.sp.ddd.canonicalmodel.publishedlanguage.snapshot.ComponentData;
import pl.mpietrewicz.sp.ddd.canonicalmodel.publishedlanguage.snapshot.ContractData;
import pl.mpietrewicz.sp.ddd.canonicalmodel.publishedlanguage.snapshot.premium.ComponentPremiumSnapshot;
import pl.mpietrewicz.sp.ddd.canonicalmodel.publishedlanguage.snapshot.premium.PremiumSnapshot;
import pl.mpietrewicz.sp.ddd.sharedkernel.Amount;
import pl.mpietrewicz.sp.ddd.support.infrastructure.repo.BaseAggregateRoot;
import pl.mpietrewicz.sp.ddd.support.domain.DomainEventPublisher;

import javax.inject.Inject;
import javax.persistence.CascadeType;
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
    private ContractData contractData;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER, orphanRemoval = true)
    @JoinColumn(name = "premium_id")
    @Fetch(FetchMode.JOIN)
    private final List<ComponentPremium> componentPremiums = new ArrayList<>();

    @Transient
    @Inject
    protected DomainEventPublisher eventPublisher;

    private boolean dailyChanges = false; // todo: zrobić z tego politykę, czy rozliczanie co do dnia

    public Premium() {
    }

    public Premium(AggregateId aggregateId, ContractData contractData, ComponentPremium basicComponentPremium) {
        this.aggregateId = aggregateId;
        this.contractData = contractData;
        this.componentPremiums.add(basicComponentPremium);
    }

    public void add(ComponentData componentData, LocalDate date, Amount amount) {
        LocalDateTime now = LocalDateTime.now();

        ComponentPremium componentPremium = getComponentPremium(componentData)
                .orElseGet(() -> {
                    ComponentPremium addedComponentPremium = new ComponentPremium(componentData);
                    componentPremiums.add(addedComponentPremium);
                    return addedComponentPremium;
                });

        if (!dailyChanges) date = YearMonth.from(date).atDay(1); // todo: w przyszłości to jakoś ładniej roziwzać
        componentPremium.addPremium(date, amount, now);

        sentEvent(date, now, "ComponentServiceImpl");
    }

    public void cancel(ComponentData componentData) {
        LocalDateTime now = LocalDateTime.now();
        ComponentPremium componentPremium = getComponentPremium(componentData)
                .orElseThrow(() -> new IllegalStateException("No component found for premium change"));

        LocalDate canceledAddDate = componentPremium.cancel(now);

        sentEvent(canceledAddDate, now, "PremiumServiceImpl");
    }

    public void change(ComponentData componentData, LocalDate date, Amount amount) {
        LocalDateTime now = LocalDateTime.now();
        ComponentPremium componentPremium = getComponentPremium(componentData)
                .orElseThrow(() -> new IllegalStateException("No component found for premium change"));

        if (!dailyChanges) date = YearMonth.from(date).atDay(1); // todo: w przyszłości to jakoś ładniej roziwzać
        componentPremium.changePremium(date, amount, now);

        sentEvent(date, now, "PremiumServiceImpl");
    }

    public void delete(ComponentData componentData, LocalDate date) {
        LocalDateTime now = LocalDateTime.now();
        ComponentPremium componentPremium = getComponentPremium(componentData)
                .orElseThrow(() -> new IllegalStateException("No component found for premium delete"));

        if (!dailyChanges) date = YearMonth.from(date).atEndOfMonth(); // todo: w przyszłości to jakoś ładniej roziwzać
        componentPremium.deletePremium(date, now);

        sentEvent(date, now, "ComponentServiceImpl");
    }

    private Optional<ComponentPremium> getComponentPremium(ComponentData componentData) {
        return componentPremiums.stream()
                .filter(cp -> cp.applay(componentData))
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
                .contractData(contractData)
                .componentPremiumSnapshots(componentPremiumSnapshots)
                .build();
    }

    private void sentEvent(LocalDate date, LocalDateTime timestamp, String serviceName) {
        PremiumChangedEvent event = new PremiumChangedEvent(contractData, date, timestamp);
        eventPublisher.publish(event, serviceName);
    }

}