package pl.mpietrewicz.sp.ddd.canonicalmodel.publishedlanguage;

import lombok.Builder;
import lombok.Getter;
import pl.mpietrewicz.sp.ddd.sharedkernel.Divisor;

@Builder
@Getter
public class RiskDefinition {

    private final Long id;
    private final Divisor premiumDivisor;

}