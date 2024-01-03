package pl.mpietrewicz.sp.modules.balance.domain.balance;

import lombok.NoArgsConstructor;
import pl.mpietrewicz.sp.ddd.annotations.domain.DomainEntity;
import pl.mpietrewicz.sp.modules.balance.domain.balance.month.ComponentPremium;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@DomainEntity
@Embeddable
@NoArgsConstructor
public class Premium {

    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "operation_id")
    private List<ComponentPremium> componentPremiums = new ArrayList<>();

    public Premium(List<ComponentPremium> componentPremiums) {
        this.componentPremiums = componentPremiums;
    }

    public Premium createCopy() {
        List<ComponentPremium> componentPremiumsCopy = this.componentPremiums.stream()
                .map(ComponentPremium::createCopy)
                .collect(Collectors.toList());
        return new Premium(componentPremiumsCopy);
    }

    public List<ComponentPremium> getComponentPremiums() {
        return componentPremiums;
    }

    public void update(ComponentPremium componentPremium) {
        componentPremiums.removeIf(c -> c.isAppliedTo(componentPremium)); // todo: jeśli nie ma takiej składki to zwrócić wyjątek dal ChangePremium
        componentPremiums.add(componentPremium);
    }


}