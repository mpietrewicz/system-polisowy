package pl.mpietrewicz.sp.modules.contract.domain.premium.operation;

import pl.mpietrewicz.sp.ddd.annotations.domain.ValueObject;

import javax.persistence.Embeddable;
import java.time.LocalDateTime;

@ValueObject
@Embeddable
public class Cancelation {

    private final LocalDateTime registration = LocalDateTime.now();

    public boolean isHappenedBefore(LocalDateTime timestamp) {
        return this.registration.compareTo(timestamp) <= 0;
    }

}