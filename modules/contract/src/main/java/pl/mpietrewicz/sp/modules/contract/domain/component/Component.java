package pl.mpietrewicz.sp.modules.contract.domain.component;

import pl.mpietrewicz.sp.ddd.annotations.domain.AggregateRoot;
import pl.mpietrewicz.sp.ddd.support.domain.BaseAggregateRoot;
import pl.mpietrewicz.sp.SystemParameters;
import pl.mpietrewicz.sp.ddd.canonicalmodel.publishedlanguage.AggregateId;
import pl.mpietrewicz.sp.ddd.canonicalmodel.publishedlanguage.ComponentStatus;
import pl.mpietrewicz.sp.ddd.canonicalmodel.publishedlanguage.snapshot.ComponentData;
import pl.mpietrewicz.sp.ddd.canonicalmodel.publishedlanguage.snapshot.ContractData;

import javax.persistence.CascadeType;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import java.time.LocalDate;

@Entity
@AggregateRoot
public class Component extends BaseAggregateRoot {

    @Embedded
    private ContractData contractData;

    private LocalDate startDate;

    @Enumerated(EnumType.STRING)
    private ComponentStatus componentStatus;

    @Enumerated(EnumType.STRING)
    private ComponentType componentType;

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER, orphanRemoval = true)
    @JoinColumn(name = "liability_id")
    private Liability liability;

    public Component() {
    }

    public Component(AggregateId aggregateId, ContractData contractData, LocalDate startDate, ComponentType componentType,
                     Liability liability) {
        this.aggregateId = aggregateId;
        this.contractData = contractData;
        this.startDate = startDate;
        this.componentStatus = ComponentStatus.OPEN; // todo: do obsłużenia
        this.componentType = componentType;
        this.liability = liability;
    }

    public void terminate(LocalDate terminatedDate) {
        liability.end(terminatedDate);
        if (isBeforeCurrentAccountingMonth(terminatedDate)) {
            changeComponentStatus(ComponentStatus.CLOSE);
        }
    }

    public ComponentData generateSnapshot() {
        return new ComponentData(aggregateId, contractData, startDate, componentStatus);
    }

    public ContractData getContractData() {
        return contractData;
    }

    public boolean isContractOpen() {
        return componentStatus.equals(ComponentStatus.OPEN);
    }

    public boolean isAdditional() {
        return this.componentType == ComponentType.ADDITIONAL;
    }

    private boolean isBeforeCurrentAccountingMonth(LocalDate date) {
        return date.compareTo(SystemParameters.getCurrentAccountingMonth().atDay(1)) <= 0;
    }

    private void changeComponentStatus(ComponentStatus componentStatus) {
        this.componentStatus = componentStatus;
    }
}