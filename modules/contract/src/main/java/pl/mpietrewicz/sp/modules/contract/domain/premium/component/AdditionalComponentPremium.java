package pl.mpietrewicz.sp.modules.contract.domain.premium.component;

import lombok.NoArgsConstructor;
import pl.mpietrewicz.sp.ddd.canonicalmodel.publishedlanguage.AggregateId;
import pl.mpietrewicz.sp.modules.contract.domain.premium.ChangePremiumPolicyEnum;
import pl.mpietrewicz.sp.modules.contract.domain.premium.operation.AddPremium;
import pl.mpietrewicz.sp.modules.contract.domain.premium.operation.Operation;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@DiscriminatorValue("ADDITIONAL")
@NoArgsConstructor
public class AdditionalComponentPremium extends ComponentPremium {

    public AdditionalComponentPremium(AggregateId componentId, AddPremium addPremium,
                                      ChangePremiumPolicyEnum changePremiumPolicyEnum) {
        super(componentId, addPremium, changePremiumPolicyEnum);
    }

    @Override
    public LocalDate cancel(LocalDateTime timestamp) {
        List<Operation> validOperations = getValidOperations(timestamp);
        Operation addOperation = getAddOperation(validOperations);
        addOperation.cancel();

        return addOperation.getDate();
    }

}