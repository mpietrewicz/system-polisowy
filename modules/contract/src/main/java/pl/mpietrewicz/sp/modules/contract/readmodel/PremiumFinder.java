package pl.mpietrewicz.sp.modules.contract.readmodel;

import lombok.RequiredArgsConstructor;
import pl.mpietrewicz.sp.ddd.annotations.application.Finder;
import pl.mpietrewicz.sp.ddd.canonicalmodel.publishedlanguage.AggregateId;
import pl.mpietrewicz.sp.ddd.canonicalmodel.publishedlanguage.snapshot.premium.PremiumSnapshot;
import pl.mpietrewicz.sp.modules.contract.application.api.PremiumService;
import pl.mpietrewicz.sp.modules.contract.readmodel.model.Premium;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Finder
@RequiredArgsConstructor
public class PremiumFinder {

    private final PremiumService premiumService;

    public Premium find(AggregateId contractId) {
        PremiumSnapshot premiumSnapshot = premiumService.getPremiumSnapshot(contractId, LocalDateTime.now());
        BigDecimal amount = premiumSnapshot.getCurrentPremium().getValue();
        LocalDate validFrom = premiumSnapshot.getValidFrom();

        return createPremium(amount, validFrom);
    }

    public Premium find(AggregateId contractId, AggregateId componentId) {
        PremiumSnapshot premiumSnapshot = premiumService.getPremiumSnapshot(contractId, LocalDateTime.now());
        BigDecimal amount = premiumSnapshot.getCurrentPremium(componentId).getValue();
        LocalDate validFrom = premiumSnapshot.getValidFrom(componentId);

        return createPremium(amount, validFrom);
    }

    private Premium createPremium(BigDecimal amount, LocalDate validFrom) {
        return Premium.builder()
                .amount(amount)
                .validFrom(validFrom)
                .build();
    }

}