package pl.mpietrewicz.sp.modules.contract.domain.premium;

import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import pl.mpietrewicz.sp.ddd.canonicalmodel.publishedlanguage.snapshot.ComponentData;
import pl.mpietrewicz.sp.ddd.canonicalmodel.publishedlanguage.snapshot.premium.ChangePremiumSnapshot;
import pl.mpietrewicz.sp.ddd.canonicalmodel.publishedlanguage.snapshot.premium.ComponentPremiumSnapshot;
import pl.mpietrewicz.sp.ddd.sharedkernel.Amount;
import pl.mpietrewicz.sp.modules.contract.ddd.support.domain.BaseEntity;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static java.util.function.Predicate.not;
import static java.util.stream.Collectors.toList;

@Embeddable
@Entity
public class ComponentPremium extends BaseEntity {

    @Embedded
    private ComponentData componentData;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER, orphanRemoval = true)
    @JoinColumn(name = "component_premium_id")
    @Fetch(FetchMode.JOIN)
    private final List<Operation> operations = new ArrayList<>();

    public ComponentPremium() {
    }

    public ComponentPremium(ComponentData componentData) {
        this.componentData = componentData;
    }

    protected void addPremium(LocalDate date, Amount amount, LocalDateTime timestamp) {
        if (hasAlreadyAdded(timestamp)) {
            throw new IllegalStateException("Component premium has already added!");
        }
        operations.add(new Operation(Type.ADD, date, amount, timestamp));
    }

    protected void changePremium(LocalDate date, Amount amount, LocalDateTime timestamp) {
        if (!hasPremiumAt(date, timestamp)) {
            throw new IllegalStateException("Not found premium to change at date: " + date);
        }
        operations.add(new Operation(Type.CHANGE, date, amount, timestamp));
    }

    protected void deletePremium(LocalDate date, LocalDateTime timestamp) {
        if (!hasPremiumAt(date, timestamp)) {
            throw new IllegalStateException("Not found premium to delete at date: " + date);
        }
        operations.add(new Operation(Type.DELETE, date, Amount.ZERO, timestamp));
    }

    public LocalDate cancel(LocalDateTime timestamp) {
        List<Operation> validOperations = getValidOperations(timestamp);
        Operation addOperation = getAddOperation(validOperations);
        addOperation.cancel();

        return addOperation.getDate();
    }

    protected boolean hasAlreadyAdded(LocalDateTime timestamp) {
        return operations.stream()
                .filter(o -> o.getType() == Type.ADD)
                .anyMatch(not(o -> o.isCanceled(timestamp)));
    }

    protected Optional<ComponentPremiumSnapshot> generateSnapshot(LocalDateTime timestamp) {
        if (!hasAlreadyAdded(timestamp)) return Optional.empty();

        List<Operation> validOperations = getValidOperations(timestamp);
        Operation addOperation = getAddOperation(validOperations);
        List<ChangePremiumSnapshot> changesSnapshot = getChangeOperations(validOperations).stream()
                .map(Operation::getChangePremiumSnapshot)
                .collect(toList());

        LocalDate end = validOperations.stream()
                .filter(o -> o.getType() == Type.DELETE)
                .findAny()
                .map(Operation::getDate)
                .orElse(null);

        return Optional.of(ComponentPremiumSnapshot.builder()
                .componentId(componentData.getAggregateId())
                .start(addOperation.getDate())
                .initialAmount(addOperation.getAmount())
                .changes(changesSnapshot)
                .end(end)
                .build());
    }

    private List<Operation> getChangeOperations(List<Operation> validOperations) {
        return validOperations.stream()
                .filter(o -> o.getType() == Type.CHANGE)
                .collect(toList());
    }

    private Operation getAddOperation(List<Operation> validOperations) {
        return validOperations.stream()
                .filter(o -> o.getType() == Type.ADD)
                .findFirst()
                .orElseThrow();
    }

    protected boolean applay(ComponentData componentData) {
        return this.componentData.getAggregateId().equals(componentData.getAggregateId());
    }

    private boolean hasPremiumAt(LocalDate date, LocalDateTime timestamp) {
        return generateSnapshot(timestamp)
                .map(snapshot -> snapshot.getPremiumAt(date).isPositive())
                .orElse(false);
    }

    private List<Operation> getValidOperations(LocalDateTime timestamp) {
        return operations.stream()
                .filter(o -> o.registeredBefore(timestamp))
                .filter(not(o -> o.isCanceled(timestamp)))
                .collect(toList());
    }

}