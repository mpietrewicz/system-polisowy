package pl.mpietrewicz.sp.modules.contract.domain.premium.operation;

import lombok.NoArgsConstructor;
import pl.mpietrewicz.sp.ddd.annotations.domain.ValueObject;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import java.time.LocalDate;
import java.time.LocalDateTime;

@ValueObject
@Entity
@DiscriminatorValue("DELETE")
@NoArgsConstructor
public class DeletePremium extends Operation {

    public DeletePremium(LocalDate date, LocalDateTime timestamp) {
        super(date, null, timestamp);
    }

    @Override
    public Type getType() {
        return Type.DELETE;
    }

}