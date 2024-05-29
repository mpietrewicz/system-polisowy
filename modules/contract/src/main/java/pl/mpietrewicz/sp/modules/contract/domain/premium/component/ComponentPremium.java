package pl.mpietrewicz.sp.modules.contract.domain.premium.component;

import pl.mpietrewicz.sp.ddd.canonicalmodel.publishedlanguage.AggregateId;
import pl.mpietrewicz.sp.ddd.canonicalmodel.publishedlanguage.snapshot.premium.ChangePremiumSnapshot;
import pl.mpietrewicz.sp.ddd.canonicalmodel.publishedlanguage.snapshot.premium.ComponentPremiumSnapshot;
import pl.mpietrewicz.sp.ddd.sharedkernel.valueobject.PositiveAmount;
import pl.mpietrewicz.sp.ddd.support.infrastructure.repo.BaseEntity;
import pl.mpietrewicz.sp.modules.contract.domain.premium.ChangePremiumPolicyEnum;
import pl.mpietrewicz.sp.modules.contract.domain.premium.operation.AddPremium;
import pl.mpietrewicz.sp.modules.contract.domain.premium.operation.ChangePremium;
import pl.mpietrewicz.sp.modules.contract.domain.premium.operation.DeletePremium;
import pl.mpietrewicz.sp.modules.contract.domain.premium.operation.Operation;
import pl.mpietrewicz.sp.modules.contract.domain.premium.operation.Type;
import pl.mpietrewicz.sp.modules.contract.domain.premium.policy.ChangePremiumPolicyFactory;

import javax.persistence.AttributeOverride;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static java.util.function.Predicate.not;
import static java.util.stream.Collectors.toList;

@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
public abstract class ComponentPremium extends BaseEntity {

    @Embedded
    @AttributeOverride(name = "aggregateId", column = @Column(name = "componentId", nullable = false))
    private AggregateId componentId;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER, orphanRemoval = true)
    @JoinColumn(name = "component_premium_id")
    private List<Operation> operations;

    @Enumerated(EnumType.STRING)
    private ChangePremiumPolicyEnum changePremiumPolicyEnum;

    public ComponentPremium() {
    }

    public ComponentPremium(AggregateId componentId, AddPremium addPremium, ChangePremiumPolicyEnum changePremiumPolicyEnum) {
        this.componentId = componentId;
        this.operations = new ArrayList<>();
        this.operations.add(addPremium);
        this.changePremiumPolicyEnum = changePremiumPolicyEnum;
    }

    public void changePremium(LocalDate date, PositiveAmount premium, LocalDateTime timestamp) {
        if (!hasPremiumAt(date, timestamp)) {
            throw new IllegalStateException("Not found premium to change at date: " + date);
        } else if (!ChangePremiumPolicyFactory.create(changePremiumPolicyEnum).isAvailable(operations, date)) {
            throw new IllegalStateException("Change premium unavailable!");
        }

        operations.add(new ChangePremium(date, premium, timestamp));
    }

    public void deletePremium(LocalDate date, LocalDateTime timestamp) {
        if (getValidOperations(timestamp).isEmpty()) {
            throw new IllegalStateException("Not found premium to delete at date: " + date);
        }
        operations.add(new DeletePremium(date, timestamp));
    }

    public abstract LocalDate cancel(LocalDateTime timestamp);

    protected boolean hasAlreadyAdded(LocalDateTime timestamp) {
        return operations.stream()
                .filter(o -> o.getType() == Type.ADD)
                .anyMatch(not(o -> o.isCanceled(timestamp)));
    }

    public Optional<ComponentPremiumSnapshot> generateSnapshot(LocalDateTime timestamp) {
        if (!hasAlreadyAdded(timestamp)) return Optional.empty();

        List<Operation> validOperations = getValidOperations(timestamp);
        Operation addOperation = getAddOperation(validOperations);
        List<ChangePremiumSnapshot> changesSnapshot = getChangeOperations(validOperations).stream()
                .map(Operation::getChangePremiumSnapshot)
                .collect(toList());
        LocalDate end = getEndPremiumDate(validOperations);

        return Optional.of(ComponentPremiumSnapshot.builder()
                .componentId(componentId)
                .start(addOperation.getDate())
                .initialPremium(addOperation.getPremium())
                .changes(changesSnapshot)
                .end(end)
                .build());
    }

    public boolean applay(AggregateId componentId) {
        return this.componentId.equals(componentId);
    }

    protected Operation getAddOperation(List<Operation> validOperations) {
        return validOperations.stream()
                .filter(o -> o.getType() == Type.ADD)
                .findFirst()
                .orElseThrow();
    }

    protected List<Operation> getValidOperations(LocalDateTime timestamp) {
        return operations.stream()
                .filter(o -> o.registeredBefore(timestamp))
                .filter(not(o -> o.isCanceled(timestamp)))
                .collect(toList());
    }

    private boolean hasPremiumAt(LocalDate date, LocalDateTime timestamp) {
        return generateSnapshot(timestamp)
                .map(snapshot -> snapshot.getPremiumAt(date).isPresent())
                .orElse(false);
    }

    private List<Operation> getChangeOperations(List<Operation> validOperations) {
        return validOperations.stream()
                .filter(o -> o.getType() == Type.CHANGE)
                .collect(toList());
    }

    private LocalDate getEndPremiumDate(List<Operation> validOperations) {
        return validOperations.stream()
                .filter(o -> o.getType() == Type.DELETE)
                .findAny()
                .map(Operation::getDate)
                .orElse(null);
    }

}