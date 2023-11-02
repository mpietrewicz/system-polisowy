package pl.mpietrewicz.sp.modules.contract.domain.premium;

import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import pl.mpietrewicz.sp.ddd.annotations.domain.AggregateRoot;
import pl.mpietrewicz.sp.ddd.canonicalmodel.publishedlanguage.AggregateId;
import pl.mpietrewicz.sp.ddd.canonicalmodel.publishedlanguage.snapshot.ComponentData;
import pl.mpietrewicz.sp.modules.contract.ddd.support.domain.BaseAggregateRoot;
import pl.mpietrewicz.sp.modules.contract.domain.contract.ChangePremiumException;

import javax.persistence.CascadeType;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Entity
@AggregateRoot
public class Premium extends BaseAggregateRoot {

    @Embedded
    private ComponentData componentData;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER, orphanRemoval = true)
    @JoinColumn(name = "premium_id")
    @Fetch(FetchMode.JOIN)
    private List<PremiumHistory> premiumHistory = new ArrayList<>();

    public Premium() {
    }

    public Premium(AggregateId aggregateId, ComponentData componentData, PremiumHistory premiumHistory) {
        this.aggregateId = aggregateId;
        this.componentData = componentData;
        this.premiumHistory.add(premiumHistory);
    }

    public void changePremium(BigDecimal premiumAmount, LocalDate since) {
        if (isPremiumAfter(since)) {
            throw new ChangePremiumException("Nie można zmienić składki - istnieją późniejsze zmiany");
        } else {
            add(since, premiumAmount);
        }
    }

    private boolean isPremiumAfter(LocalDate since) {
        return premiumHistory.stream()
                .map(PremiumHistory::getSince)
                .anyMatch(s -> s.isAfter(since));
    }

    private void add(LocalDate startDate, BigDecimal premiumAmount) {
        premiumHistory.add(new PremiumHistory(startDate, premiumAmount));
    }

    public BigDecimal getPremiumAmount(LocalDate atDate) { // todo: powinno być pobieranie dla konkretnego periodu!
        return premiumHistory.stream()
                .filter(ph -> ph.getSince().compareTo(atDate) <= 0)
                .max(PremiumHistory::premiumDateComparator)
                .map(PremiumHistory::getMonthlyAmount)
                .orElseThrow(() -> new RuntimeException("Nie ma składki na dzień" + atDate));
    }

    public Map<LocalDate, BigDecimal> getPremiumHistory(LocalDate since) {
        return premiumHistory.stream()
                .filter(ph -> ph.getSince().compareTo(since) >= 0)
                .collect(Collectors.toMap(PremiumHistory::getSince, PremiumHistory::getMonthlyAmount));
    }

    public BigDecimal getStartingAmount() {
        return premiumHistory.stream()
                .findFirst()
                .map(PremiumHistory::getMonthlyAmount)
                .orElseThrow();
    }

    public ComponentData getComponentData() {
        return componentData;
    }
}