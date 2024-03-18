package pl.mpietrewicz.sp.modules.contract.domain.premium.component;

import lombok.NoArgsConstructor;
import pl.mpietrewicz.sp.ddd.canonicalmodel.publishedlanguage.AggregateId;
import pl.mpietrewicz.sp.modules.contract.domain.premium.ChangePremiumPolicyEnum;
import pl.mpietrewicz.sp.modules.contract.domain.premium.operation.AddPremium;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@DiscriminatorValue("BASIC")
@NoArgsConstructor
public class BasicComponentPremium extends ComponentPremium {

    public BasicComponentPremium(AggregateId componentId, AddPremium addPremium,
                                 ChangePremiumPolicyEnum changePremiumPolicyEnum) {
        super(componentId, addPremium, changePremiumPolicyEnum);
    }

    @Override
    public LocalDate cancel(LocalDateTime timestamp) {
        throw new UnsupportedOperationException(); // todo: w przyszłości obsłużyć bardziej biznesowo
    }

}