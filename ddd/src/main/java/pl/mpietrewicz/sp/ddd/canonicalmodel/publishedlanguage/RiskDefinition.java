package pl.mpietrewicz.sp.ddd.canonicalmodel.publishedlanguage;

import lombok.Builder;
import lombok.Getter;
import pl.mpietrewicz.sp.ddd.sharedkernel.valueobject.Divisor;

@Builder
@Getter
public class RiskDefinition {

    private final Long id;
    private final String name;
    private final Divisor premiumDivisor;

}